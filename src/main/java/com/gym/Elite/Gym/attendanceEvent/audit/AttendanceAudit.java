package com.gym.Elite.Gym.attendanceEvent.audit;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable audit trail for all attendance mutations and events.
 * Supports Members, Trainers, and future actors.
 */
@Entity
@Table(
        name = "attendance_audit",
        indexes = {
                @Index(name = "idx_audit_tenant", columnList = "tenant_id"),
                @Index(name = "idx_audit_attendance", columnList = "attendance_id"),
                @Index(name = "idx_audit_actor", columnList = "actor_id, actor_type"),
                @Index(name = "idx_audit_created", columnList = "created_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AttendanceAudit extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "attendance_id")
    private UUID attendanceId;

    @Column(name = "actor_id", nullable = false)
    private UUID actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false)
    private AttendanceActorType actorType;

    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    private String modifiedBy;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    private AttendanceSource source;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
