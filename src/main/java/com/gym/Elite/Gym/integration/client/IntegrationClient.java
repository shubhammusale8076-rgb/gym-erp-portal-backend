package com.gym.Elite.Gym.integration.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.integration.dto.EventRequest;
import com.gym.Elite.Gym.integration.entity.EventOutbox;
import com.gym.Elite.Gym.integration.repo.EventOutboxRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class IntegrationClient {

    private final RestTemplate restTemplate;

    @Autowired
    private EventOutboxRepo outboxRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${integration.service.url}")
    private String integrationServiceUrl;

    public IntegrationClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Async
    public void sendEvent(String eventId, String eventType, String tenantId, Object payload) {
        log.info("Sending event: {} ID: {}", eventType, eventId);

        EventRequest request = EventRequest.builder()
                .eventId(eventId)
                .eventType(eventType)
                .tenantId(tenantId)
                .payload(payload)
                .build();

        try {
            restTemplate.postForEntity(integrationServiceUrl, request, String.class);
            updateOutboxStatus(eventId, "SENT");
            log.info("Event {} sent successfully", eventId);
        } catch (Exception e) {
            log.error("Failed to send event {}: {}", eventId, e.getMessage());
            updateOutboxStatus(eventId, "FAILED");
        }
    }

    private void updateOutboxStatus(String eventId, String status) {
        outboxRepo.findById(eventId).ifPresent(outbox -> {
            outbox.setStatus(status);
            outbox.setProcessedAt(new Date());
            outboxRepo.save(outbox);
        });
    }

    @Scheduled(fixedDelay = 60000) // Retry every minute
    public void retryFailedEvents() {
        List<EventOutbox> failedEvents = outboxRepo.findByStatus("FAILED");
        if (failedEvents.isEmpty()) return;

        log.info("Retrying {} failed events", failedEvents.size());
        for (EventOutbox event : failedEvents) {
            if (event.getRetryCount() >= 5) {
                log.error("Max retries reached for event {}", event.getEventId());
                continue;
            }

            try {
                Object payload = objectMapper.readValue(event.getPayload(), Object.class);

                EventRequest request = EventRequest.builder()
                        .eventId(event.getEventId())
                        .eventType(event.getEventType())
                        .tenantId(event.getTenantId())
                        .payload(payload)
                        .build();

                restTemplate.postForEntity(integrationServiceUrl, request, String.class);

                event.setStatus("SENT");
                event.setProcessedAt(new Date());
                outboxRepo.save(event);

            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                outboxRepo.save(event);
                log.error("Retry failed for event {}: {}", event.getEventId(), e.getMessage());
            }
        }
    }
}
