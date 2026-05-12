package com.gym.Elite.Gym.crm.logging.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_integration_action_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;
    private String action;

    @Column(columnDefinition = "TEXT")
    private String requestPayload;

    @Column(columnDefinition = "TEXT")
    private String responsePayload;

    @Enumerated(EnumType.STRING)
    private LogStatus status;

    private int retryCount;
    private String correlationId;
    private java.util.UUID tenantId;
    private LocalDateTime createdAt;

    public enum LogStatus {
        PENDING, PROCESSING, SUCCESS, FAILED, RETRYING
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
