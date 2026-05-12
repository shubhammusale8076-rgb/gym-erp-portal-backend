package com.gym.Elite.Gym.crm.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseCrmEvent {
    private String eventId;
    private UUID tenantId;
    private String correlationId;
    private UUID leadId;
    private LocalDateTime timestamp;
}
