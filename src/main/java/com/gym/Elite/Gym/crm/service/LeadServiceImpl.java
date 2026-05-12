package com.gym.Elite.Gym.crm.service;

import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.crm.dto.*;
import com.gym.Elite.Gym.crm.entity.*;
import com.gym.Elite.Gym.crm.enums.*;
import com.gym.Elite.Gym.crm.exception.DuplicateLeadException;
import com.gym.Elite.Gym.crm.exception.InvalidStageTransitionException;
import com.gym.Elite.Gym.crm.exception.LeadNotFoundException;
import com.gym.Elite.Gym.crm.repository.*;
import com.gym.Elite.Gym.crm.specification.LeadSpecification;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LeadServiceImpl implements LeadService {

    private final LeadRepository        leadRepository;
    private final LeadActivityRepository activityRepository;
    private final LeadNoteRepository    noteRepository;
    private final LeadTaskRepository    taskRepository;
    private final FollowUpRepository    followUpRepository;
    
    private final com.gym.Elite.Gym.crm.outbox.service.OutboxService outboxService;
    private final com.gym.Elite.Gym.crm.assignment.service.AssignmentEngine assignmentEngine;
    private final FeatureFlagService featureFlagService;

    // =========================================================================
    // CREATE
    // =========================================================================

    @Override
    public LeadResponseDto createLead(LeadCreateRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        // Duplicate phone check (unique per tenant)
        if (leadRepository.existsByPhoneAndTenantIdAndDeletedFalse(request.getPhone(), tenantId)) {
            throw new DuplicateLeadException(
                "A lead with phone '" + request.getPhone() + "' already exists for this gym.");
        }

        // Determine initial score from source
        int initialScore = scoreForSource(request.getSource());

        LeadStage initialStage = request.getStage() != null ? request.getStage() : LeadStage.NEW_LEAD;

        Lead lead = Lead.builder()
                .tenantId(tenantId)
                .fullName(request.getFullName().trim())
                .phone(request.getPhone().trim())
                .email(request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null)
                .source(request.getSource())
                .stage(initialStage)
                .leadScore(initialScore)
                .priority(calculatePriority(initialScore))
                .expectedRevenue(request.getExpectedRevenue())
                .fitnessGoal(request.getFitnessGoal())
                .nextAction(request.getNextAction())
                .nextFollowUpAt(request.getNextFollowUpAt())
                .assignedTo(request.getAssignedTo() != null ? User.builder().id(request.getAssignedTo()).build() : null)
                .converted(false)
                .deleted(false)
                .followUpOverdue(false)
                .createdBy(getCurrentUsername())
                .updatedBy(getCurrentUsername())
                .build();

        lead = leadRepository.save(lead);

        // ── Auto-Assignment ───────────────────────────────────────────────────
        if (lead.getAssignedTo() == null && 
            featureFlagService.isEnabled(tenantId, TenantFeatureFlag.FeatureName.AUTO_ASSIGNMENT)) {
            assignmentEngine.autoAssign(lead);
        }

        // ── Outbox: LeadCreatedEvent ──────────────────────────────────────────
        com.gym.Elite.Gym.crm.event.LeadCreatedEvent event = com.gym.Elite.Gym.crm.event.LeadCreatedEvent.builder()
                .tenantId(tenantId)
                .correlationId(org.slf4j.MDC.get(com.gym.Elite.Gym.crm.util.CorrelationIdFilter.CORRELATION_ID_LOG_VAR))
                .leadId(lead.getId())
                .name(lead.getFullName())
                .phone(lead.getPhone())
                .source(lead.getSource().name())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        outboxService.saveEvent(event, "LEAD", lead.getId().toString(), tenantId);

        // ── Activity: LEAD_CREATED ────────────────────────────────────────────
        createActivity(lead, ActivityType.LEAD_CREATED,
                "Lead Created",
                "New lead '" + lead.getFullName() + "' captured from " + lead.getSource().name());

        log.info("CRM: Lead created [tenantId={}, leadId={}, phone={}]",
                tenantId, lead.getId(), lead.getPhone());

        return toResponseDto(lead);
    }

    // =========================================================================
    // READ (paginated + filtered)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public Page<LeadResponseDto> getLeads(String search, LeadStage stage, LeadSource source,
                                           UUID assignedTo, Pageable pageable) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        Specification<Lead> spec = LeadSpecification.buildFilter(tenantId, search, stage, source, assignedTo);
        return leadRepository.findAll(spec, pageable).map(this::toResponseDto);
    }

    // =========================================================================
    // READ (single with full detail)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public LeadDetailsDto getLeadById(UUID id) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        Lead lead = fetchLead(id, tenantId);

        List<FollowUpResponseDto> followUps = followUpRepository
                .findByLeadIdAndTenantIdOrderByFollowUpAtAsc(id, tenantId)
                .stream().map(this::toFollowUpDto).toList();

        List<ActivityResponseDto> activities = activityRepository
                .findByLeadIdAndTenantIdOrderByCreatedAtDesc(id, tenantId)
                .stream().map(this::toActivityDto).toList();

        List<NoteResponseDto> notes = noteRepository
                .findByLeadIdAndTenantIdOrderByCreatedAtDesc(id, tenantId)
                .stream().map(this::toNoteDto).toList();

        List<TaskResponseDto> tasks = taskRepository
                .findByLeadIdAndTenantIdOrderByDueDateAsc(id, tenantId)
                .stream().map(this::toTaskDto).toList();

        long pendingTasks    = taskRepository.countByLeadIdAndTenantIdAndCompletedFalse(id, tenantId);
        long completedFollowUps = followUpRepository.countByLeadIdAndTenantIdAndCompletedTrue(id, tenantId);

        return LeadDetailsDto.builder()
                .id(lead.getId())
                .fullName(lead.getFullName())
                .phone(lead.getPhone())
                .email(lead.getEmail())
                .source(lead.getSource())
                .stage(lead.getStage())
                .priority(lead.getPriority())
                .leadScore(lead.getLeadScore())
                .expectedRevenue(lead.getExpectedRevenue())
                .fitnessGoal(lead.getFitnessGoal())
                .nextAction(lead.getNextAction())
                .nextFollowUpAt(lead.getNextFollowUpAt())
                .followUpOverdue(lead.getFollowUpOverdue())
                .converted(lead.getConverted())
                .assignedTo(lead.getAssignedTo() != null ? lead.getAssignedTo().getId() : null)
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .followUps(followUps)
                .activities(activities)
                .notes(notes)
                .tasks(tasks)
                .totalFollowUps((int) followUpRepository.countByLeadIdAndTenantId(id, tenantId))
                .completedFollowUps((int) completedFollowUps)
                .pendingTasks((int) pendingTasks)
                .build();
    }

    // =========================================================================
    // UPDATE
    // =========================================================================

    @Override
    public LeadResponseDto updateLead(UUID id, LeadUpdateRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        Lead lead = fetchLead(id, tenantId);

        // Phone uniqueness: check if new phone is taken by a DIFFERENT lead in same tenant
        if (request.getPhone() != null
                && !request.getPhone().equals(lead.getPhone())
                && leadRepository.existsByPhoneAndTenantIdAndDeletedFalseAndIdNot(
                        request.getPhone(), tenantId, id)) {
            throw new DuplicateLeadException(
                "Phone '" + request.getPhone() + "' is already assigned to another lead.");
        }

        lead.setFullName(request.getFullName().trim());
        if (request.getPhone()    != null) lead.setPhone(request.getPhone().trim());
        if (request.getEmail()    != null) lead.setEmail(request.getEmail().trim().toLowerCase());
        if (request.getSource()   != null) lead.setSource(request.getSource());
        if (request.getExpectedRevenue() != null) lead.setExpectedRevenue(request.getExpectedRevenue());
        if (request.getFitnessGoal() != null)     lead.setFitnessGoal(request.getFitnessGoal());
        if (request.getNextAction()  != null)     lead.setNextAction(request.getNextAction());
        if (request.getNextFollowUpAt() != null)  lead.setNextFollowUpAt(request.getNextFollowUpAt());
        if (request.getAssignedTo()  != null)     lead.setAssignedTo(User.builder().id(request.getAssignedTo()).build());
        lead.setUpdatedBy(getCurrentUsername());

        lead = leadRepository.save(lead);

        // ── Activity ─────────────────────────────────────────────────────────
        createActivity(lead, ActivityType.LEAD_UPDATED, "Lead Updated",
                "Lead profile updated by " + getCurrentUsername());

        return toResponseDto(lead);
    }

    // =========================================================================
    // SOFT DELETE
    // =========================================================================

    @Override
    public void deleteLead(UUID id) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        Lead lead = fetchLead(id, tenantId);
        lead.setDeleted(true);
        lead.setUpdatedBy(getCurrentUsername());
        leadRepository.save(lead);
        log.info("CRM: Lead soft-deleted [tenantId={}, leadId={}]", tenantId, id);
    }

    // =========================================================================
    // KANBAN STAGE UPDATE
    // =========================================================================

    @Override
    public LeadResponseDto updateStage(UUID id, StageUpdateRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        Lead lead = fetchLead(id, tenantId);

        LeadStage oldStage = lead.getStage();
        LeadStage newStage = request.getStage();

        validateStageTransition(oldStage, newStage);

        lead.setStage(newStage);
        lead.setUpdatedBy(getCurrentUsername());

        // ── Score adjustments on stage change ─────────────────────────────────
        int delta = scoreForStageChange(newStage);
        applyScoreDelta(lead, delta);

        lead = leadRepository.save(lead);

        // ── Activity ─────────────────────────────────────────────────────────
        createActivity(lead, ActivityType.STAGE_CHANGED,
                "Stage Changed",
                "Stage moved from " + oldStage.name() + " → " + newStage.name());

        // ── Auto-convert if stage = CONVERTED ────────────────────────────────
        if (newStage == LeadStage.CONVERTED) {
            lead.setConverted(true);
            leadRepository.save(lead);
            createActivity(lead, ActivityType.LEAD_CONVERTED, "Lead Converted",
                    "Lead has been marked as converted successfully");
            
            // ── Outbox: LeadConvertedEvent ────────────────────────────────────
            publishEvent(lead, com.gym.Elite.Gym.crm.event.LeadConvertedEvent.class);
        } else {
            // ── Outbox: StageChangedEvent (Future) ─────────────────────────────
        }

        return toResponseDto(lead);
    }

    // =========================================================================
    // EXPLICIT CONVERSION
    // =========================================================================

    @Override
    public LeadResponseDto convertLead(UUID id) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        Lead lead = fetchLead(id, tenantId);

        if (Boolean.TRUE.equals(lead.getConverted())) {
            throw new InvalidStageTransitionException("Lead is already converted.");
        }

        lead.setStage(LeadStage.CONVERTED);
        lead.setConverted(true);
        lead.setUpdatedBy(getCurrentUsername());
        applyScoreDelta(lead, 40);   // trial/conversion bonus
        lead = leadRepository.save(lead);

        // ── Outbox: LeadConvertedEvent ────────────────────────────────────────
        publishEvent(lead, com.gym.Elite.Gym.crm.event.LeadConvertedEvent.class);

        createActivity(lead, ActivityType.LEAD_CONVERTED,
                "Lead Converted 🎉",
                "Lead '" + lead.getFullName() + "' has been converted to a member. "
                + "All lead history preserved.");

        log.info("CRM: Lead converted [tenantId={}, leadId={}]", tenantId, id);
        return toResponseDto(lead);
    }

    private void publishEvent(Lead lead, Class<? extends com.gym.Elite.Gym.crm.event.BaseCrmEvent> eventClass) {
        try {
            com.gym.Elite.Gym.crm.event.BaseCrmEvent event = com.gym.Elite.Gym.crm.event.BaseCrmEvent.class.cast(
                    eventClass.getMethod("builder").invoke(null)); // This is complex, I'll just use a helper method per event or a builder factory
            
            // Re-implementing correctly for LeadConvertedEvent specifically for now
            if (eventClass.equals(com.gym.Elite.Gym.crm.event.LeadConvertedEvent.class)) {
                 com.gym.Elite.Gym.crm.event.LeadConvertedEvent convertedEvent = com.gym.Elite.Gym.crm.event.LeadConvertedEvent.builder()
                        .tenantId(lead.getTenantId())
                        .correlationId(org.slf4j.MDC.get(com.gym.Elite.Gym.crm.util.CorrelationIdFilter.CORRELATION_ID_LOG_VAR))
                        .leadId(lead.getId())
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
                 outboxService.saveEvent(convertedEvent, "LEAD", lead.getId().toString(), lead.getTenantId());
            }
        } catch (Exception e) {
            log.error("Failed to publish event via outbox", e);
        }
    }

    // =========================================================================
    // KANBAN BOARD — all non-deleted leads grouped by stage
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<LeadKanbanDto> getKanbanBoard() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        Specification<Lead> spec = LeadSpecification.buildFilter(tenantId, null, null, null, null);
        return leadRepository.findAll(spec).stream()
                .map(this::toKanbanDto)
                .toList();
    }

    // =========================================================================
    // ── SCORING ENGINE ────────────────────────────────────────────────────────
    // =========================================================================

    /**
     * Initial score awarded based on acquisition source.
     * Walk-in leads are highest quality signal.
     */
    private int scoreForSource(LeadSource source) {
        return switch (source) {
            case WALK_IN   -> 25;
            case REFERRAL  -> 20;
            case PHONE_CALL -> 15;
            case WHATSAPP  -> 12;
            case INSTAGRAM -> 10;
            case FACEBOOK  -> 10;
            case WEBSITE   -> 8;
            case OTHER     -> 5;
        };
    }

    /**
     * Score delta applied when a lead moves to a new stage.
     */
    private int scoreForStageChange(LeadStage stage) {
        return switch (stage) {
            case TRIAL_SCHEDULED -> 40;
            case NEGOTIATION     -> 20;
            case CONVERTED       -> 30;
            case CONTACTED       -> 10;
            case FOLLOW_UP       -> 5;
            case LOST            -> -25;
            default              -> 0;
        };
    }

    /**
     * Applies a score delta and recalculates priority.
     * Score is clamped to [0, 100].
     */
    void applyScoreDelta(Lead lead, int delta) {
        int newScore = Math.max(0, Math.min(100, lead.getLeadScore() + delta));
        lead.setLeadScore(newScore);
        lead.setPriority(calculatePriority(newScore));
    }

    /**
     * AUTO PRIORITY ENGINE — maps score to priority band.
     */
    LeadPriority calculatePriority(int score) {
        if (score >= 80) return LeadPriority.HOT;
        if (score >= 50) return LeadPriority.WARM;
        return LeadPriority.COLD;
    }

    // =========================================================================
    // ── STAGE TRANSITION VALIDATOR ────────────────────────────────────────────
    // =========================================================================

    /**
     * Validates that a stage transition is business-logically sound.
     * A lost lead cannot be reopened without going through NEW_LEAD first.
     */
    private void validateStageTransition(LeadStage current, LeadStage target) {
        if (current == LeadStage.CONVERTED) {
            throw new InvalidStageTransitionException(
                "Converted leads cannot change stage. Create a new lead instead.");
        }
        if (current == LeadStage.LOST && target != LeadStage.NEW_LEAD) {
            throw new InvalidStageTransitionException(
                "Lost leads can only be reopened as NEW_LEAD.");
        }
    }

    // =========================================================================
    // ── ACTIVITY HELPER ───────────────────────────────────────────────────────
    // =========================================================================

    void createActivity(Lead lead, ActivityType type, String title, String description) {
        LeadActivity activity = LeadActivity.builder()
                .lead(lead)
                .tenantId(lead.getTenantId())
                .type(type)
                .title(title)
                .description(description)
                .createdBy(getCurrentUsername())
                .build();
        activityRepository.save(activity);
    }

    // =========================================================================
    // ── PRIVATE HELPERS ───────────────────────────────────────────────────────
    // =========================================================================

    private Lead fetchLead(UUID id, UUID tenantId) {
        return leadRepository.findByIdAndTenantIdAndDeletedFalse(id, tenantId)
                .orElseThrow(() -> new LeadNotFoundException(
                    "Lead not found with id: " + id));
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    // =========================================================================
    // ── MAPPING HELPERS ───────────────────────────────────────────────────────
    // =========================================================================

    private LeadResponseDto toResponseDto(Lead l) {
        return LeadResponseDto.builder()
                .id(l.getId())
                .fullName(l.getFullName())
                .phone(l.getPhone())
                .email(l.getEmail())
                .source(l.getSource())
                .stage(l.getStage())
                .priority(l.getPriority())
                .leadScore(l.getLeadScore())
                .expectedRevenue(l.getExpectedRevenue())
                .fitnessGoal(l.getFitnessGoal())
                .nextAction(l.getNextAction())
                .nextFollowUpAt(l.getNextFollowUpAt())
                .followUpOverdue(l.getFollowUpOverdue())
                .converted(l.getConverted())
                .assignedTo(l.getAssignedTo() != null ? l.getAssignedTo().getId() : null)
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .build();
    }

    private LeadKanbanDto toKanbanDto(Lead l) {
        return LeadKanbanDto.builder()
                .id(l.getId())
                .fullName(l.getFullName())
                .phone(l.getPhone())
                .stage(l.getStage())
                .priority(l.getPriority())
                .leadScore(l.getLeadScore())
                .source(l.getSource())
                .assignedTo(l.getAssignedTo() != null ? l.getAssignedTo().getId() : null)
                .followUpOverdue(l.getFollowUpOverdue())
                .nextFollowUpAt(l.getNextFollowUpAt())
                .expectedRevenue(l.getExpectedRevenue())
                .build();
    }

    FollowUpResponseDto toFollowUpDto(FollowUp f) {
        return FollowUpResponseDto.builder()
                .id(f.getId())
                .leadId(f.getLead().getId())
                .leadName(f.getLead().getFullName())
                .followUpAt(f.getFollowUpAt())
                .type(f.getType())
                .status(f.getStatus())
                .remarks(f.getRemarks())
                .completed(f.getCompleted())
                .overdue(f.getOverdue())
                .assignedTo(f.getAssignedTo() != null ? f.getAssignedTo().getId() : null)
                .createdAt(f.getCreatedAt())
                .build();
    }

    ActivityResponseDto toActivityDto(LeadActivity a) {
        return ActivityResponseDto.builder()
                .id(a.getId())
                .leadId(a.getLead().getId())
                .type(a.getType())
                .title(a.getTitle())
                .description(a.getDescription())
                .createdBy(a.getCreatedBy())
                .createdAt(a.getCreatedAt())
                .build();
    }

    NoteResponseDto toNoteDto(LeadNote n) {
        return NoteResponseDto.builder()
                .id(n.getId())
                .leadId(n.getLead().getId())
                .note(n.getNote())
                .createdBy(n.getCreatedBy())
                .createdAt(n.getCreatedAt())
                .build();
    }

    TaskResponseDto toTaskDto(LeadTask t) {
        return TaskResponseDto.builder()
                .id(t.getId())
                .leadId(t.getLead().getId())
                .title(t.getTitle())
                .completed(t.getCompleted())
                .dueDate(t.getDueDate())
                .assignedTo(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
                .createdAt(t.getCreatedAt())
                .build();
    }
}
