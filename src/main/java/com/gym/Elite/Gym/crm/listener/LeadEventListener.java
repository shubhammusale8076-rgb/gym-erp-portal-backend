package com.gym.Elite.Gym.crm.listener;

import com.gym.Elite.Gym.crm.automation.service.WorkflowEngine;
import com.gym.Elite.Gym.crm.event.LeadCreatedEvent;
import com.gym.Elite.Gym.crm.event.LeadAssignedEvent;
import com.gym.Elite.Gym.crm.event.LeadTrialScheduledEvent;
import com.gym.Elite.Gym.crm.event.LeadConvertedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeadEventListener {

    private final WorkflowEngine workflowEngine;

    @Async("crmAutomationExecutor")
    @EventListener
    public void onLeadCreated(LeadCreatedEvent event) {
        log.info("LeadCreatedEvent received for lead: {}", event.getLeadId());
        workflowEngine.processEvent(event);
    }

    @Async("crmAutomationExecutor")
    @EventListener
    public void onLeadAssigned(LeadAssignedEvent event) {
        log.info("LeadAssignedEvent received for lead: {}", event.getLeadId());
        workflowEngine.processEvent(event);
    }

    @Async("crmAutomationExecutor")
    @EventListener
    public void onTrialScheduled(LeadTrialScheduledEvent event) {
        log.info("LeadTrialScheduledEvent received for lead: {}", event.getLeadId());
        workflowEngine.processEvent(event);
    }

    @Async("crmAutomationExecutor")
    @EventListener
    public void onLeadConverted(LeadConvertedEvent event) {
        log.info("LeadConvertedEvent received for lead: {}", event.getLeadId());
        workflowEngine.processEvent(event);
    }
}
