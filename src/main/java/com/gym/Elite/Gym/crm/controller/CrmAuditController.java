package com.gym.Elite.Gym.crm.controller;

import com.gym.Elite.Gym.crm.communication.entity.LeadCommunication;
import com.gym.Elite.Gym.crm.communication.repository.LeadCommunicationRepository;
import com.gym.Elite.Gym.crm.logging.entity.IntegrationActionLog;
import com.gym.Elite.Gym.crm.logging.repository.IntegrationActionLogRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/crm/audit")
@RequiredArgsConstructor
public class CrmAuditController {

    private final LeadCommunicationRepository communicationRepository;
    private final IntegrationActionLogRepository integrationLogRepository;

    @GetMapping("/communications/{leadId}")
    public ResponseEntity<List<LeadCommunication>> getLeadCommunications(@PathVariable UUID leadId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return ResponseEntity.ok(communicationRepository.findByLeadIdAndTenantIdOrderByCreatedAtDesc(leadId, tenantId));
    }

    @GetMapping("/integrations")
    public ResponseEntity<List<IntegrationActionLog>> getIntegrationLogs(@RequestParam(required = false) String correlationId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        if (correlationId != null) {
            return ResponseEntity.ok(integrationLogRepository.findByCorrelationIdAndTenantIdOrderByCreatedAtDesc(correlationId, tenantId));
        }
        return ResponseEntity.ok(integrationLogRepository.findByTenantIdOrderByCreatedAtDesc(tenantId));
    }
}
