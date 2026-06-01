package com.gym.Elite.Gym.internal.controller;

import com.gym.Elite.Gym.internal.dto.PaymentConfirmRequest;
import com.gym.Elite.Gym.payment.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/payments")
@RequiredArgsConstructor
@Slf4j
public class InternalPaymentController {

    private final PaymentTransactionService transactionService;

    @Value("${internal.api.secret}")
    private String internalSecret;

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestHeader(value = "X-Internal-Secret", required = false) String secret,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationIdHeader,
            @RequestBody PaymentConfirmRequest request) {

        log.info("Received request to confirm payment internally");

        if (secret == null || !secret.equals(internalSecret)) {
            log.warn("Unauthorized attempt to access internal payment confirmation endpoint");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid or missing internal secret"));
        }

        String correlationId = correlationIdHeader;
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = MDC.get("correlationId");
        }
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put("correlationId", correlationId);

        try {
            transactionService.confirmPayment(request);
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Payment confirmed and membership activated successfully"
            ));
        } catch (Exception e) {
            log.error("Internal payment confirmation failed. Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } finally {
            MDC.remove("correlationId");
        }
    }
}
