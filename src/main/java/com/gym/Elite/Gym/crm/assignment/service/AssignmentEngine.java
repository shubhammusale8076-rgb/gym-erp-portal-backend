package com.gym.Elite.Gym.crm.assignment.service;

import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.auth.repo.UserRepo;
import com.gym.Elite.Gym.crm.assignment.entity.LeadAssignmentHistory;
import com.gym.Elite.Gym.crm.assignment.repository.LeadAssignmentHistoryRepository;
import com.gym.Elite.Gym.crm.entity.Lead;
import com.gym.Elite.Gym.crm.outbox.service.OutboxService;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentEngine {

    private final LeadRepository leadRepository;
    private final UserRepo userRepo;
    private final LeadAssignmentHistoryRepository historyRepository;
    private final OutboxService outboxService;

    @Transactional
    public void autoAssign(Lead lead) {
        log.info("Auto-assigning lead: {} for tenant: {}", lead.getId(), lead.getTenantId());

        // Simple Round-Robin implementation
        List<User> eligibleStaff = userRepo.findByTenantIdAndEnabled(lead.getTenantId(), true);
        
        if (eligibleStaff.isEmpty()) {
            log.warn("No eligible staff found for auto-assignment in tenant: {}", lead.getTenantId());
            return;
        }

        // Logic to find the staff with the least workload or next in line
        User assignedTo = eligibleStaff.get(0); // Placeholder for round-robin logic
        
        assignToStaff(lead, assignedTo, "AUTO_ASSIGNMENT");
    }

    @Transactional
    public void assignToStaff(Lead lead, User staff, String reason) {
        User previousStaff = lead.getAssignedTo();
        
        lead.setAssignedTo(staff);

        // ── Outbox: LeadAssignedEvent ──────────────────────────────────────────
        com.gym.Elite.Gym.crm.event.LeadAssignedEvent event = com.gym.Elite.Gym.crm.event.LeadAssignedEvent.builder()
                .tenantId(lead.getTenantId())
                .correlationId(org.slf4j.MDC.get(com.gym.Elite.Gym.crm.util.CorrelationIdFilter.CORRELATION_ID_LOG_VAR))
                .leadId(lead.getId())
                .assignedToId(staff.getId())
                .assignedToName(staff.getFullName())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        outboxService.saveEvent(event, "LEAD", lead.getId().toString(), lead.getTenantId());

        leadRepository.save(lead);

        LeadAssignmentHistory history = LeadAssignmentHistory.builder()
                .lead(lead)
                .previousAssignedToId(previousStaff != null ? previousStaff.getId() : null)
                .previousAssignedToName(previousStaff != null ? previousStaff.getFullName() : "UNASSIGNED")
                .newAssignedToId(staff.getId())
                .newAssignedToName(staff.getFullName())
                .changedBy("SYSTEM")
                .reason(reason)
                .build();

        historyRepository.save(history);
        log.info("Lead {} assigned to {} (Reason: {})", lead.getId(), staff.getFullName(), reason);
    }
}
