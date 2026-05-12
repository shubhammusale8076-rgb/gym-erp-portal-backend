package com.gym.Elite.Gym.crm.assignment.controller;

import com.gym.Elite.Gym.crm.assignment.entity.LeadAssignmentHistory;
import com.gym.Elite.Gym.crm.assignment.repository.LeadAssignmentHistoryRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/crm/assignment/history")
@RequiredArgsConstructor
public class AssignmentController {

    private final LeadAssignmentHistoryRepository historyRepository;

    @GetMapping("/{leadId}")
    public ResponseEntity<List<LeadAssignmentHistory>> getLeadAssignmentHistory(@PathVariable UUID leadId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return ResponseEntity.ok(historyRepository.findByLeadIdAndTenantIdOrderByCreatedAtDesc(leadId, tenantId));
    }
}
