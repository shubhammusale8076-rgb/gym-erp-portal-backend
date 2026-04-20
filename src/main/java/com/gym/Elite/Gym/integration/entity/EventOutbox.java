package com.gym.Elite.Gym.integration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    private String status; // PENDING, SENT, FAILED

    @Builder.Default
    private int retryCount = 0;

    private Date createdAt;
    private Date processedAt;
}
