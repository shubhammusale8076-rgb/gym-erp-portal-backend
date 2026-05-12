package com.gym.Elite.Gym.crm.outbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.Elite.Gym.crm.outbox.entity.OutboxEvent;
import com.gym.Elite.Gym.crm.outbox.repository.OutboxEventRepository;
import com.gym.Elite.Gym.crm.util.CorrelationIdFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvent(Object event, String aggregateType, String aggregateId, UUID tenantId) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_LOG_VAR);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(event.getClass().getSimpleName())
                    .payload(payload)
                    .status(OutboxEvent.OutboxStatus.PENDING)
                    .tenantId(tenantId)
                    .correlationId(correlationId)
                    .build();

            repository.save(outboxEvent);
            log.debug("Event saved to outbox: {} for aggregate: {}", event.getClass().getSimpleName(), aggregateId);
        } catch (Exception e) {
            log.error("Failed to save event to outbox", e);
            throw new RuntimeException("Outbox persistence failed", e);
        }
    }
}
