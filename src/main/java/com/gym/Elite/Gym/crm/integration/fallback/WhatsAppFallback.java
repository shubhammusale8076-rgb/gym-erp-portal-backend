package com.gym.Elite.Gym.crm.integration.fallback;

import com.gym.Elite.Gym.crm.integration.client.WhatsAppIntegrationClient;
import com.gym.Elite.Gym.crm.integration.dto.EventResponse;
import com.gym.Elite.Gym.crm.integration.dto.WhatsAppRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WhatsAppFallback implements WhatsAppIntegrationClient {

    @Override
    public EventResponse sendMessage(WhatsAppRequest request) {
        log.error("WhatsApp Integration Service is unavailable. CorrelationId: {}", request.getCorrelationId());
        return EventResponse.builder()
                .status("FAILED_PROVIDER_UNAVAILABLE")
                .errorMessage("Integration service is currently down. Request queued for retry.")
                .build();
    }
}
