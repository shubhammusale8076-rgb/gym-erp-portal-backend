package com.gym.Elite.Gym.integration.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebhookEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenants tenant;

    private String source; // RAZORPAY, WHATSAPP, STRIPE

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String externalEventId; // from provider (IMPORTANT for idempotency)

    @Enumerated(EnumType.STRING)
    private EventStatus status; // PENDING, PROCESSING, DONE, FAILED

    private Integer retryCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}