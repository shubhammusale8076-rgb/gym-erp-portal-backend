package com.gym.Elite.Gym.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "processed_webhooks")
public class ProcessedWebhook {

    public ProcessedWebhook(String eventId) {
        this.eventId = eventId;
    }

    @Id
    @Column(unique = true, nullable = false)
    private String eventId;

    @CreationTimestamp
    private LocalDateTime processedAt;
}
