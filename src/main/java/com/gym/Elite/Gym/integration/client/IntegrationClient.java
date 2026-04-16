package com.gym.Elite.Gym.integration.client;

import com.gym.Elite.Gym.integration.dto.EventRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class IntegrationClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${integration.service.url}")
    private String integrationServiceUrl;

    @Async
    public void sendEvent(String eventType, String tenantId, Object payload) {
        log.info("Sending event: {} for tenant: {}", eventType, tenantId);

        EventRequest request = EventRequest.builder()
                .eventType(eventType)
                .tenantId(tenantId)
                .payload(payload)
                .build();

        try {
            restTemplate.postForEntity(integrationServiceUrl, request, String.class);
            log.info("Event {} sent successfully", eventType);
        } catch (Exception e) {
            log.error("Failed to send event {}: {}", eventType, e.getMessage());
        }
    }
}
