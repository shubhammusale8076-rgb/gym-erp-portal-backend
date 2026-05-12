package com.gym.Elite.Gym.crm.listener;

import com.gym.Elite.Gym.crm.event.WhatsAppMessageRequestedEvent;
import com.gym.Elite.Gym.crm.event.CalendarBookingRequestedEvent;
import com.gym.Elite.Gym.crm.integration.client.WhatsAppIntegrationClient;
import com.gym.Elite.Gym.crm.integration.client.GoogleCalendarIntegrationClient;
import com.gym.Elite.Gym.crm.integration.dto.CalendarRequest;
import com.gym.Elite.Gym.crm.integration.dto.EventResponse;
import com.gym.Elite.Gym.crm.integration.dto.WhatsAppRequest;
import com.gym.Elite.Gym.crm.communication.entity.LeadCommunication;
import com.gym.Elite.Gym.crm.communication.repository.LeadCommunicationRepository;
import com.gym.Elite.Gym.crm.logging.entity.IntegrationActionLog;
import com.gym.Elite.Gym.crm.logging.repository.IntegrationActionLogRepository;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import com.gym.Elite.Gym.integration.dto.IntegrationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IntegrationRequestListener {

    private final WhatsAppIntegrationClient whatsappClient;
    private final GoogleCalendarIntegrationClient calendarClient;
    private final LeadCommunicationRepository communicationRepository;
    private final IntegrationActionLogRepository actionLogRepository;
    private final LeadRepository leadRepository;

    @Async("integrationExecutor")
    @EventListener
    public void onWhatsAppRequested(WhatsAppMessageRequestedEvent event) {
        log.info("Processing WhatsApp request for lead: {}", event.getLeadId());
        
        WhatsAppRequest request = WhatsAppRequest.builder()
                .tenantId(event.getTenantId())
                .correlationId(event.getCorrelationId())
                .leadId(event.getLeadId())
                .phone(event.getPhone())
                .template(event.getTemplate())
                .variables(event.getVariables())
                .build();

        EventResponse response = whatsappClient.sendMessage(request);
        
        logIntegrationAction("WHATSAPP", "SEND_MESSAGE", request, response, event.getCorrelationId(), event.getTenantId());
        
        logCommunication(event, "WHATSAPP", request.getTemplate(), response);
    }

    @Async("integrationExecutor")
    @EventListener
    public void onCalendarRequested(CalendarBookingRequestedEvent event) {
        log.info("Processing Calendar request for lead: {}", event.getLeadId());

        CalendarRequest request = CalendarRequest.builder()
                .tenantId(event.getTenantId())
                .correlationId(event.getCorrelationId())
                .leadId(event.getLeadId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .attendees(event.getAttendees())
                .build();

        EventResponse response = calendarClient.createEvent(request);

        logIntegrationAction("GOOGLE_CALENDAR", "CREATE_EVENT", request, response, event.getCorrelationId(), event.getTenantId());
        
        logCommunication(event, "GOOGLE_CALENDAR", event.getTitle(), response);
    }

    private void logIntegrationAction(String provider, String action, Object request, EventResponse response, String correlationId, UUID tenantId) {
        IntegrationActionLog logEntry = IntegrationActionLog.builder()
                .provider(provider)
                .action(action)
                .status(response.getStatus().contains("SUCCESS") ? IntegrationActionLog.LogStatus.SUCCESS : IntegrationActionLog.LogStatus.FAILED)
                .correlationId(correlationId)
                .tenantId(tenantId)
                .build();
        actionLogRepository.save(logEntry);
    }

    private void logCommunication(com.gym.Elite.Gym.crm.event.BaseCrmEvent event, String type, String content, EventResponse response) {
        LeadCommunication communication = LeadCommunication.builder()
                .lead(leadRepository.findById(event.getLeadId()).orElse(null))
                .type(LeadCommunication.CommunicationType.valueOf(type))
                .direction(LeadCommunication.Direction.OUTBOUND)
                .content(content)
                .status(response.getStatus())
                .externalMessageId(response.getExternalId())
                .correlationId(event.getCorrelationId())
                .tenantId(event.getTenantId())
                .build();
        communicationRepository.save(communication);
    }
}
