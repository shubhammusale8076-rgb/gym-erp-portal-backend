package com.gym.Elite.Gym.crm.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private java.util.UUID userId; // Recipient staff/admin ID
    private String title;
    private String message;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean read;
    private java.util.UUID tenantId;
    private LocalDateTime createdAt;

    public enum NotificationType {
        INFO, SUCCESS, WARNING, ERROR, LEAD_ASSIGNED, OVERDUE_TASK, INTEGRATION_FAILURE
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }
}
