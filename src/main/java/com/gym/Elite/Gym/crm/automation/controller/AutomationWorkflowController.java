package com.gym.Elite.Gym.crm.automation.controller;

import com.gym.Elite.Gym.crm.automation.entity.AutomationWorkflow;
import com.gym.Elite.Gym.crm.automation.repository.AutomationWorkflowRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/crm/automation/workflows")
@RequiredArgsConstructor
public class AutomationWorkflowController {

    private final AutomationWorkflowRepository workflowRepository;

    @GetMapping
    public ResponseEntity<List<AutomationWorkflow>> getWorkflows() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return ResponseEntity.ok(workflowRepository.findByTenantId(tenantId));
    }

    @PostMapping
    public ResponseEntity<AutomationWorkflow> createWorkflow(@RequestBody AutomationWorkflow workflow) {
        workflow.setTenantId(SecurityUtils.getCurrentTenantId());
        if (workflow.getActions() != null) {
            workflow.getActions().forEach(action -> action.setWorkflow(workflow));
        }
        return ResponseEntity.ok(workflowRepository.save(workflow));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutomationWorkflow> updateWorkflow(@PathVariable Long id, @RequestBody AutomationWorkflow request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        AutomationWorkflow workflow = workflowRepository.findById(id)
                .filter(w -> w.getTenantId().equals(tenantId))
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        workflow.setName(request.getName());
        workflow.setTriggerType(request.getTriggerType());
        workflow.setConditions(request.getConditions());
        workflow.setEnabled(request.isEnabled());
        
        // Simplified action update logic
        if (request.getActions() != null) {
            workflow.getActions().clear();
            request.getActions().forEach(action -> {
                action.setWorkflow(workflow);
                workflow.getActions().add(action);
            });
        }

        return ResponseEntity.ok(workflowRepository.save(workflow));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        workflowRepository.findById(id)
                .filter(w -> w.getTenantId().equals(tenantId))
                .ifPresent(workflowRepository::delete);
        return ResponseEntity.noContent().build();
    }
}
