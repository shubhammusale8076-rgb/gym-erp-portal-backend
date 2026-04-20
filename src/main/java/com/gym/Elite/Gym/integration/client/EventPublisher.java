package com.gym.Elite.Gym.integration.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.integration.entity.EventOutbox;
import com.gym.Elite.Gym.integration.repo.EventOutboxRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class EventPublisher {

    @Autowired
    private IntegrationClient integrationClient;

    @Autowired
    private EventOutboxRepo outboxRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public void publish(String eventType, String tenantId, Object data) {
        String eventId = UUID.randomUUID().toString();

        try {
            String jsonPayload = objectMapper.writeValueAsString(data);

            // 1. Save to Outbox first (Reliability)
            EventOutbox outbox = EventOutbox.builder()
                    .eventId(eventId)
                    .eventType(eventType)
                    .tenantId(tenantId)
                    .payload(jsonPayload)
                    .status("PENDING")
                    .createdAt(new Date())
                    .build();

            outboxRepo.save(outbox);

            // 2. Attempt async send
            integrationClient.sendEvent(eventId, eventType, tenantId, data);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event data", e);
            throw new RuntimeException("Event serialization failed");
        }
    }
}
