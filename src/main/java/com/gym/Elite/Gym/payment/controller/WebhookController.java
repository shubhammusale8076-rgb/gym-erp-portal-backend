package com.gym.Elite.Gym.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.integration.entity.WebhookEvent;
import com.gym.Elite.Gym.integration.processor.razorpay.RazorpayEventProcessor;
import com.gym.Elite.Gym.tenants.repo.TenantRefRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final RazorpayEventProcessor razorpayEventProcessor;
    private final TenantRefRepository tenantRefRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("/razorpay/{tenantId}")
    public ResponseEntity<Void> handleRazorpayWebhook(
            @PathVariable UUID tenantId,
            @RequestHeader("x-razorpay-signature") String signature,
            HttpServletRequest request) {

        try {
            // Read RAW body for signature verification
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            Map<String, Object> payload = objectMapper.readValue(body, Map.class);

            tenantRefRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));

            WebhookEvent event = new WebhookEvent();
            event.setEventType((String) payload.get("event"));
            event.setPayload(payload.get("payload"));
            event.setTenantId(tenantId);
            event.setSignature(signature);
            event.setProviderEventId((String) payload.get("event_id"));
            event.setRawBody(body);

            razorpayEventProcessor.process(event);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
