package com.gym.Elite.Gym.integration.client;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.common.config.FeignClientConfig;
import com.gym.Elite.Gym.integration.dto.google.GooglePasswordResetRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "google-integration",
        url = "${integration.service.url}",
        fallback = FeignClientConfig.class
)
public interface GoogleIntegrationClient {

    @PostMapping("/internal/google/password-reset")
    ResponseDto sendPasswordResetMessage(@RequestBody GooglePasswordResetRequestDto request);
}
