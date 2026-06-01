package com.gym.Elite.Gym.crm.integration.client;

import com.gym.Elite.Gym.common.config.FeignClientConfig;
import com.gym.Elite.Gym.crm.integration.dto.EventResponse;
import com.gym.Elite.Gym.crm.integration.dto.WhatsAppRequest;
import com.gym.Elite.Gym.integration.dto.WhatsAppDeliveryStatus;
import com.gym.Elite.Gym.integration.dto.whatsapp.WelcomeMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "whatsapp-integration", 
    url = "${integration.service.url}", 
    fallback = FeignClientConfig.class
)
public interface WhatsAppIntegrationClient {

    @PostMapping("/api/integration/whatsapp/send")
    EventResponse sendMessage(@RequestBody WhatsAppRequest request);

    @PostMapping("/internal/whatsapp/welcome-message")
    WhatsAppDeliveryStatus sendWelcomeMessage(
            @RequestHeader("X-Correlation-Id") String correlationId,
            @RequestBody WelcomeMessage request);

}
