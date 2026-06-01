package com.gym.Elite.Gym.integration.client;

import com.gym.Elite.Gym.common.config.FeignClientConfig;
import com.gym.Elite.Gym.payment.dto.PaymentAccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "integration-payment-client",
        url = "${integration.service.url}",
        fallback = FeignClientConfig.class
)
public interface IntegrationPaymentClient {

    @GetMapping("/internal/payments/access/{token}")
    PaymentAccessResponse getPaymentAccess(@PathVariable("token") String token);
}
