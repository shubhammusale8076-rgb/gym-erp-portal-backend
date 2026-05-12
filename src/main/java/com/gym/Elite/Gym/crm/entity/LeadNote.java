package com.gym.Elite.Gym.crm.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
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
    name = "crm_lead_notes",
    indexes = {
        @Index(name = "idx_note_lead_id",   columnList = "lead_id"),
        @Index(name = "idx_note_tenant_id", columnList = "tenant_id")
    }
)
public class LeadNote extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String note;

    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
