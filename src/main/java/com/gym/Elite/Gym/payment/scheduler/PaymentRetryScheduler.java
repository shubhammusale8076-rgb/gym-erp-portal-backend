package com.gym.Elite.Gym.payment.scheduler;

import com.gym.Elite.Gym.payment.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRetryScheduler {

    private final PaymentTransactionService transactionService;

    @Scheduled(fixedDelayString = "${payment.retry.interval-ms:300000}") // Default 5 minutes
    public void retryFailedPaymentIntegrations() {
        log.info("Starting scheduled payment transaction sync retries");
        try {
            transactionService.retryFailedTransactions();
        } catch (Exception e) {
            log.error("Error occurred during automated payment retry scheduler execution: {}", e.getMessage(), e);
        }
    }
}
