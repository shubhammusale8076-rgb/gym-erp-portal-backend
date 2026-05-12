package com.gym.Elite.Gym.crm.entity;

import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.common.entity.TenantAware;
import com.gym.Elite.Gym.crm.enums.FollowUpStatus;
import com.gym.Elite.Gym.crm.enums.FollowUpType;
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
    name = "crm_followups",
    indexes = {
        @Index(name = "idx_followup_lead_id",   columnList = "lead_id"),
        @Index(name = "idx_followup_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_followup_at",        columnList = "follow_up_at"),
        @Index(name = "idx_followup_completed", columnList = "completed, overdue")
    }
)
public class FollowUp extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @Column(name = "follow_up_at", nullable = false)
    private LocalDateTime followUpAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FollowUpType type = FollowUpType.CALL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FollowUpStatus status = FollowUpStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Builder.Default
    private Boolean completed = false;

    @Builder.Default
    private Boolean overdue = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
