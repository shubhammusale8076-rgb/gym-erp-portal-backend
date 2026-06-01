package com.gym.Elite.Gym.integration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "event_outbox")
public class EventOutbox {

    @Id
    private String eventId;

    private String eventType;

    private String tenantId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationType integrationType;

    private String status; // PENDING, SENT, FAILED

    @Builder.Default
    private int retryCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime nextRetryAt;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    private LocalDateTime processedAt;
}

