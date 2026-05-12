package com.gym.Elite.Gym.crm.automation.scheduler;

import com.gym.Elite.Gym.crm.outbox.entity.OutboxEvent;
import com.gym.Elite.Gym.crm.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IntegrationRetryScheduler {

    private final OutboxEventRepository outboxRepository;

    @Scheduled(cron = "0 0/15 * * * *") // Every 15 minutes
    @Transactional
    public void retryFailedEvents() {
        log.info("Starting retry job for failed outbox events");
        
        List<OutboxEvent> failedEvents = outboxRepository.findFailedEventsToRetry();
        
        for (OutboxEvent event : failedEvents) {
            log.info("Resetting status to PENDING for event ID: {} (Retry count: {})", event.getId(), event.getRetryCount());
            event.setStatus(OutboxEvent.OutboxStatus.PENDING);
            outboxRepository.save(event);
        }
    }
}
