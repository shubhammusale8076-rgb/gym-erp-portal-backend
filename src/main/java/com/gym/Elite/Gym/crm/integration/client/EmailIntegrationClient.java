package com.gym.Elite.Gym.crm.integration.client;

import com.gym.Elite.Gym.crm.integration.dto.EmailRequest;
import com.gym.Elite.Gym.crm.integration.dto.EventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "email-integration",
    url = "${integration.service.url}"
)
public interface EmailIntegrationClient {

    @PostMapping("/api/integration/email/send")
    EventResponse sendEmail(@RequestBody EmailRequest request);
}
