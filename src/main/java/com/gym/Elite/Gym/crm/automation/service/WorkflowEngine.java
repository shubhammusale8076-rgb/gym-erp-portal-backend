package com.gym.Elite.Gym.crm.automation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.crm.automation.entity.AutomationAction;
import com.gym.Elite.Gym.crm.automation.entity.AutomationWorkflow;
import com.gym.Elite.Gym.crm.automation.repository.AutomationWorkflowRepository;
import com.gym.Elite.Gym.crm.event.BaseCrmEvent;
import com.gym.Elite.Gym.crm.event.WhatsAppMessageRequestedEvent;
import com.gym.Elite.Gym.crm.event.CalendarBookingRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowEngine {

    private final AutomationWorkflowRepository workflowRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Async("crmAutomationExecutor")
    public void processEvent(BaseCrmEvent event) {
        log.info("Processing automation for event: {} (LeadId: {}, CorrelationId: {})", 
                 event.getClass().getSimpleName(), event.getLeadId(), event.getCorrelationId());

        AutomationWorkflow.TriggerType triggerType = mapEventToTriggerType(event);
        if (triggerType == null) {
            log.warn("No trigger type mapped for event: {}", event.getClass().getSimpleName());
            return;
        }

        List<AutomationWorkflow> workflows = workflowRepository.findByTriggerTypeAndEnabledAndTenantId(
                triggerType,
                true,
                event.getTenantId()
        );

        for (AutomationWorkflow workflow : workflows) {
            executeWorkflow(workflow, event);
        }
    }

    private void executeWorkflow(AutomationWorkflow workflow, BaseCrmEvent event) {
        log.debug("Executing workflow: {} for lead: {}", workflow.getName(), event.getLeadId());
        
        for (AutomationAction action : workflow.getActions()) {
            try {
                executeAction(action, event);
            } catch (Exception e) {
                log.error("Failed to execute action: {} in workflow: {}", action.getActionType(), workflow.getName(), e);
            }
        }
    }

    private void executeAction(AutomationAction action, BaseCrmEvent event) throws Exception {
        Map<String, Object> config = objectMapper.readValue(action.getActionConfig(), new TypeReference<Map<String, Object>>() {});

        switch (action.getActionType()) {
            case SEND_WHATSAPP:
                eventPublisher.publishEvent(WhatsAppMessageRequestedEvent.builder()
                        .tenantId(event.getTenantId())
                        .correlationId(event.getCorrelationId())
                        .leadId(event.getLeadId())
                        .phone((String) config.get("phone"))
                        .template((String) config.get("template"))
                        .variables((Map<String, String>) config.get("variables"))
                        .timestamp(event.getTimestamp())
                        .build());
                break;
                
            case NOTIFY_STAFF:
                // logic to create internal notification
                break;
                
            case CREATE_FOLLOWUP:
                // logic to auto-create follow-up
                break;
                
            // Add other actions...
        }
    }

    private AutomationWorkflow.TriggerType mapEventToTriggerType(BaseCrmEvent event) {
        String eventName = event.getClass().getSimpleName();
        return switch (eventName) {
            case "LeadCreatedEvent" -> AutomationWorkflow.TriggerType.LEAD_CREATED;
            case "LeadAssignedEvent" -> AutomationWorkflow.TriggerType.LEAD_ASSIGNED;
            case "LeadFollowUpOverdueEvent" -> AutomationWorkflow.TriggerType.FOLLOW_UP_OVERDUE;
            case "LeadTrialScheduledEvent" -> AutomationWorkflow.TriggerType.TRIAL_SCHEDULED;
            case "LeadConvertedEvent" -> AutomationWorkflow.TriggerType.LEAD_CONVERTED;
            default -> null;
        };
    }
}
