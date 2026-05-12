package com.gym.Elite.Gym.crm.integration.client;

import com.gym.Elite.Gym.crm.integration.dto.EventResponse;
import com.gym.Elite.Gym.crm.integration.dto.WhatsAppRequest;
import com.gym.Elite.Gym.crm.integration.fallback.WhatsAppFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "whatsapp-integration", 
    url = "${integration.service.url}", 
    fallback = WhatsAppFallback.class
)
public interface WhatsAppIntegrationClient {

    @PostMapping("/api/integration/whatsapp/send")
    EventResponse sendMessage(@RequestBody WhatsAppRequest request);
}
