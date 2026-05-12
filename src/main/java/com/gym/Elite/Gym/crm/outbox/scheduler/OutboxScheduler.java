package com.gym.Elite.Gym.crm.outbox.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.crm.outbox.entity.OutboxEvent;
import com.gym.Elite.Gym.crm.outbox.repository.OutboxEventRepository;
import com.gym.Elite.Gym.crm.util.CorrelationIdFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${crm.automation.outbox.process-interval-ms:5000}")
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> events = repository.findTop50ByStatusOrderByCreatedAtAsc(OutboxEvent.OutboxStatus.PENDING);
        
        for (OutboxEvent event : events) {
            processEvent(event);
        }
    }

    private void processEvent(OutboxEvent event) {
        MDC.put(CorrelationIdFilter.CORRELATION_ID_LOG_VAR, event.getCorrelationId());
        try {
            event.setStatus(OutboxEvent.OutboxStatus.PROCESSING);
            
            // Reconstruct and publish the event
            Class<?> eventClass = Class.forName("com.gym.Elite.Gym.crm.event." + event.getEventType());
            Object domainEvent = objectMapper.readValue(event.getPayload(), eventClass);
            
            eventPublisher.publishEvent(domainEvent);
            
            event.setStatus(OutboxEvent.OutboxStatus.COMPLETED);
            event.setProcessedAt(LocalDateTime.now());
            repository.save(event);
            
        } catch (Exception e) {
            log.error("Failed to process outbox event: {}", event.getId(), e);
            event.setStatus(OutboxEvent.OutboxStatus.FAILED);
            event.setRetryCount(event.getRetryCount() + 1);
            event.setErrorMessage(e.getMessage());
            repository.save(event);
        } finally {
            MDC.remove(CorrelationIdFilter.CORRELATION_ID_LOG_VAR);
        }
    }
}
