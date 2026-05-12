package com.gym.Elite.Gym.crm.outbox.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_outbox_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private String aggregateId;
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private int retryCount;
    private java.util.UUID tenantId;
    private String correlationId;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String errorMessage;

    public enum OutboxStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = OutboxStatus.PENDING;
        }
    }
}
