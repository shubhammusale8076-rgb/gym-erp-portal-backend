package com.gym.Elite.Gym.crm.communication.entity;

import com.gym.Elite.Gym.crm.entity.Lead;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_lead_communications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadCommunication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Enumerated(EnumType.STRING)
    private CommunicationType type;

    private String provider;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String status;
    private String externalMessageId;
    private String correlationId;
    private java.util.UUID tenantId;

    private LocalDateTime createdAt;

    public enum CommunicationType {
        WHATSAPP, SMS, EMAIL, CALL, REMINDER, SYSTEM
    }

    public enum Direction {
        INBOUND, OUTBOUND, SYSTEM
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
