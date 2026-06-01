package com.gym.Elite.Gym.crm.service;

import com.gym.Elite.Gym.auth.entity.GymUser;
import com.gym.Elite.Gym.crm.dto.FollowUpCreateRequest;
import com.gym.Elite.Gym.crm.dto.FollowUpResponseDto;
import com.gym.Elite.Gym.crm.entity.FollowUp;
import com.gym.Elite.Gym.crm.entity.Lead;
import com.gym.Elite.Gym.crm.enums.ActivityType;
import com.gym.Elite.Gym.crm.enums.FollowUpStatus;
import com.gym.Elite.Gym.crm.exception.FollowUpNotFoundException;
import com.gym.Elite.Gym.crm.exception.LeadNotFoundException;
import com.gym.Elite.Gym.crm.repository.FollowUpRepository;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FollowUpService {

    private final FollowUpRepository   followUpRepository;
    private final LeadRepository       leadRepository;
    private final LeadServiceImpl      leadService;   // reuse activity + score helpers

    // ── CREATE ────────────────────────────────────────────────────────────────

    public FollowUpResponseDto createFollowUp(FollowUpCreateRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Lead lead = leadRepository.findByIdAndTenantIdAndDeletedFalse(request.getLeadId(), tenantId)
                .orElseThrow(() -> new LeadNotFoundException(
                    "Lead not found: " + request.getLeadId()));

        FollowUp followUp = FollowUp.builder()
                .tenantId(tenantId)
                .lead(lead)
                .followUpAt(request.getFollowUpAt())
                .type(request.getType())
                .status(FollowUpStatus.SCHEDULED)
                .remarks(request.getRemarks())
                .completed(false)
                .overdue(false)
                .assignedTo(request.getAssignedTo() != null ? GymUser.builder().id(request.getAssignedTo()).build() : null)
                .build();

        followUp = followUpRepository.save(followUp);

        // Update lead's next follow-up if this is sooner
        if (lead.getNextFollowUpAt() == null || request.getFollowUpAt().isBefore(lead.getNextFollowUpAt())) {
            lead.setNextFollowUpAt(request.getFollowUpAt());
            leadRepository.save(lead);
        }

        // ── Score: +5 for scheduling follow-up ────────────────────────────
        leadService.applyScoreDelta(lead, 5);
        leadRepository.save(lead);

        // ── Activity ──────────────────────────────────────────────────────
        leadService.createActivity(lead, ActivityType.FOLLOW_UP_ADDED,
                "Follow-Up Scheduled",
                "Follow-up via " + request.getType().name() + " scheduled for "
                + request.getFollowUpAt().toLocalDate());

        return toDto(followUp);
    }

    // ── TODAY'S FOLLOW-UPS ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FollowUpResponseDto> getTodayFollowUps() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay   = startOfDay.plusDays(1);
        return followUpRepository.findTodayFollowUps(tenantId, startOfDay, endOfDay)
                .stream().map(this::toDto).toList();
    }

    // ── OVERDUE FOLLOW-UPS ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FollowUpResponseDto> getOverdueFollowUps() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return followUpRepository.findOverdueFollowUps(tenantId)
                .stream().map(this::toDto).toList();
    }

    // ── COMPLETE FOLLOW-UP ────────────────────────────────────────────────────

    public FollowUpResponseDto completeFollowUp(UUID followUpId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        FollowUp followUp = followUpRepository.findByIdAndTenantId(followUpId, tenantId)
                .orElseThrow(() -> new FollowUpNotFoundException(
                    "Follow-up not found: " + followUpId));

        followUp.setCompleted(true);
        followUp.setStatus(FollowUpStatus.COMPLETED);
        followUp = followUpRepository.save(followUp);

        Lead lead = followUp.getLead();

        // ── Score: +10 for completing a follow-up; -15 if it was overdue ──
        int delta = Boolean.TRUE.equals(followUp.getOverdue()) ? -15 + 10 : 10;
        leadService.applyScoreDelta(lead, delta);
        leadRepository.save(lead);

        // ── Activity ──────────────────────────────────────────────────────
        leadService.createActivity(lead, ActivityType.FOLLOW_UP_COMPLETED,
                "Follow-Up Completed",
                "Follow-up via " + followUp.getType().name() + " marked as completed by "
                + getCurrentUsername());

        return toDto(followUp);
    }

    // ── MAPPING ───────────────────────────────────────────────────────────────

    private FollowUpResponseDto toDto(FollowUp f) {
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

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
