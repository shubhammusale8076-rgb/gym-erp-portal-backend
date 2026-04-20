package com.gym.Elite.Gym.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_webhooks")
public class ProcessedWebhook {
    @Id
    @Column(unique = true, nullable = false)
    private String eventId;

    private Date processedAt;
}
