package com.gym.Elite.Gym.crm.assignment.service;

import com.gym.Elite.Gym.auth.entity.GymUser;
import com.gym.Elite.Gym.auth.repo.GymUserRepo;
import com.gym.Elite.Gym.crm.assignment.entity.LeadAssignmentHistory;
import com.gym.Elite.Gym.crm.assignment.repository.LeadAssignmentHistoryRepository;
import com.gym.Elite.Gym.crm.entity.Lead;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentEngine {

    private final LeadRepository leadRepository;
    private final GymUserRepo gymUserRepo;
    private final LeadAssignmentHistoryRepository historyRepository;

    @Transactional
    public void autoAssign(Lead lead) {
        log.info("Auto-assigning lead: {} for tenant: {}", lead.getId(), lead.getTenantId());

        List<GymUser> eligibleStaff = gymUserRepo.findByTenantIdAndEnabled(lead.getTenantId(), true);

        if (eligibleStaff.isEmpty()) {
            log.warn("No eligible staff found for auto-assignment in tenant: {}", lead.getTenantId());
            return;
        }

        GymUser assignedTo = eligibleStaff.get(0);
        assignToStaff(lead, assignedTo, "AUTO_ASSIGNMENT");
    }

    @Transactional
    public void assignToStaff(Lead lead, GymUser staff, String reason) {
        GymUser previousStaff = lead.getAssignedTo();

        lead.setAssignedTo(staff);
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
        history.setTenantId(lead.getTenantId());

        historyRepository.save(history);
        log.info("Lead {} assigned to {} (Reason: {})", lead.getId(), staff.getFullName(), reason);
    }
}
