package com.gym.Elite.Gym.crm.assignment.service;

import com.gym.Elite.Gym.crm.assignment.entity.LeadAssignmentHistory;
import com.gym.Elite.Gym.crm.assignment.repository.LeadAssignmentHistoryRepository;
import com.gym.Elite.Gym.crm.entity.Lead;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import com.gym.Elite.Gym.entity.User;
import com.gym.Elite.Gym.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final LeadAssignmentHistoryRepository historyRepository;

    @Transactional
    public void autoAssign(Lead lead) {
        log.info("Auto-assigning lead: {} for tenant: {}", lead.getId(), lead.getTenantId());

        // Simple Round-Robin implementation
        List<User> eligibleStaff = userRepository.findByTenantIdAndActive(lead.getTenantId(), true);
        
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
