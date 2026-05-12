package com.gym.Elite.Gym.crm.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import com.gym.Elite.Gym.crm.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(
    name = "crm_lead_activities",
    indexes = {
        @Index(name = "idx_activity_lead_id",   columnList = "lead_id"),
        @Index(name = "idx_activity_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_activity_created_at", columnList = "created_at")
    }
)
public class LeadActivity extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
