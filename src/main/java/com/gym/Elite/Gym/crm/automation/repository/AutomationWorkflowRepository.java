package com.gym.Elite.Gym.crm.automation.repository;

import com.gym.Elite.Gym.crm.automation.entity.AutomationWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.UUID;

@Repository
public interface AutomationWorkflowRepository extends JpaRepository<AutomationWorkflow, Long> {
    List<AutomationWorkflow> findByTriggerTypeAndEnabledAndTenantId(AutomationWorkflow.TriggerType triggerType, boolean enabled, UUID tenantId);
    List<AutomationWorkflow> findByTenantId(UUID tenantId);
}
