package com.gym.Elite.Gym.crm.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import com.gym.Elite.Gym.crm.enums.LeadPriority;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(
    name = "crm_leads",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_lead_phone_tenant", columnNames = {"phone", "tenant_id"})
    },
    indexes = {
        @Index(name = "idx_lead_tenant_id",   columnList = "tenant_id"),
        @Index(name = "idx_lead_stage",        columnList = "tenant_id, stage"),
        @Index(name = "idx_lead_source",       columnList = "tenant_id, source"),
        @Index(name = "idx_lead_assigned_to",  columnList = "tenant_id, assigned_to"),
        @Index(name = "idx_lead_deleted",      columnList = "tenant_id, deleted"),
        @Index(name = "idx_lead_phone",        columnList = "phone, tenant_id")
    }
)
public class Lead extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number format")
    @Column(nullable = false)
    private String phone;

    @Email(message = "Invalid email address")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeadSource source = LeadSource.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeadStage stage = LeadStage.NEW_LEAD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeadPriority priority = LeadPriority.COLD;

    @Builder.Default
    private Integer leadScore = 0;

    private Double expectedRevenue;

    @Column(columnDefinition = "TEXT")
    private String fitnessGoal;

    @Column(columnDefinition = "TEXT")
    private String nextAction;

    private LocalDateTime nextFollowUpAt;

    @Builder.Default
    private Boolean followUpOverdue = false;

    @Builder.Default
    private Boolean converted = false;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    // ─── Relationships ──────────────────────────────────────────────────────────

    @Builder.Default
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FollowUp> followUps = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LeadActivity> activities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LeadNote> notes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LeadTask> tasks = new ArrayList<>();
}
