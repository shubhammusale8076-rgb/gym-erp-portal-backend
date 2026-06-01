package com.gym.Elite.Gym.crm.scheduler;

import com.gym.Elite.Gym.crm.entity.FollowUp;
import com.gym.Elite.Gym.crm.entity.Lead;
import com.gym.Elite.Gym.crm.repository.FollowUpRepository;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OVERDUE FOLLOW-UP ENGINE
 *
 * Runs every hour (configurable via fixedRateString).
 * - Scans all incomplete follow-ups whose scheduled time has passed
 * - Marks them as overdue = true
 * - Updates the parent lead's followUpOverdue flag
 * - Applies a score penalty of -20 to the lead
 *
 * Designed to be idempotent — repeated runs never double-penalise.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueFollowUpScheduler {

    private final FollowUpRepository followUpRepository;
    private final LeadRepository     leadRepository;
    private final com.gym.Elite.Gym.crm.outbox.service.OutboxService outboxService;

    @Scheduled(fixedRateString = "${crm.scheduler.overdue-check-interval-ms:3600000}")
    @Transactional
    public void markOverdueFollowUps() {
        LocalDateTime now = LocalDateTime.now();
        log.info("CRM Scheduler: Running overdue follow-up check at {}", now);

        // Fetch all pending (not yet marked overdue) follow-ups past their scheduled time
        List<FollowUp> overdueFollowUps = followUpRepository.findAllPendingOverdueGlobal(now);

        if (overdueFollowUps.isEmpty()) {
            log.debug("CRM Scheduler: No overdue follow-ups found.");
            return;
        }

        log.warn("CRM Scheduler: Found {} overdue follow-ups — processing...", overdueFollowUps.size());

        // Mark each follow-up as overdue
        for (FollowUp followUp : overdueFollowUps) {
            followUp.setOverdue(true);
        }
        followUpRepository.saveAll(overdueFollowUps);

        // Group by lead so we can apply the score penalty once per lead
        Map<UUID, List<FollowUp>> groupedByLead = overdueFollowUps.stream()
                .collect(Collectors.groupingBy(f -> f.getLead().getId()));

        List<UUID> leadIds = overdueFollowUps.stream()
                .map(f -> f.getLead().getId())
                .distinct()
                .toList();

        List<Lead> leads = leadRepository.findAllById(leadIds);

        for (Lead lead : leads) {
            // Mark lead-level overdue flag
            lead.setFollowUpOverdue(true);

            // Apply score penalty: -20 per overdue event (clamped to min 0)
            List<FollowUp> leadOverdueFollowUps = groupedByLead.getOrDefault(lead.getId(), List.of());
            long overdueCount = leadOverdueFollowUps.size();
            int penalty = (int) Math.min(20L * overdueCount, lead.getLeadScore());
            lead.setLeadScore(lead.getLeadScore() - penalty);

            // Recalculate priority after penalty
            lead.setPriority(recalculatePriority(lead.getLeadScore()));

            // ── Outbox: LeadFollowUpOverdueEvent ──────────────────────────────
            for (FollowUp f : leadOverdueFollowUps) {
                com.gym.Elite.Gym.crm.event.LeadFollowUpOverdueEvent event = com.gym.Elite.Gym.crm.event.LeadFollowUpOverdueEvent.builder()
                        .tenantId(lead.getTenantId())
                        .correlationId("SCHEDULER-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                        .leadId(lead.getId())
                        .followUpId(f.getId())
                        .scheduledAt(f.getFollowUpAt())
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
                outboxService.saveEvent(event, "FOLLOW_UP", f.getId().toString(), lead.getTenantId());
            }
        }

        leadRepository.saveAll(leads);

        log.warn("CRM Scheduler: Processed overdue flags for {} leads.", leads.size());
    }

    private com.gym.Elite.Gym.crm.enums.LeadPriority recalculatePriority(int score) {
        if (score >= 80) return com.gym.Elite.Gym.crm.enums.LeadPriority.HOT;
        if (score >= 50) return com.gym.Elite.Gym.crm.enums.LeadPriority.WARM;
        return com.gym.Elite.Gym.crm.enums.LeadPriority.COLD;
    }
}
