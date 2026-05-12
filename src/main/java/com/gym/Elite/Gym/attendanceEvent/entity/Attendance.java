package com.gym.Elite.Gym.attendanceEvent.entity;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus;
import com.gym.Elite.Gym.auth.entity.SessionType;
import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Unified attendance record for any actor type (Member, Trainer, Staff, etc.).
 *
 * Refactored to use Actor Type Architecture:
 * - Replaces memberId with generic actorId + actorType.
 * - Allows a single table to handle heterogeneous attendance events.
 */
@Entity
@Table(
        name = "attendance",
        indexes = {
                @Index(name = "idx_att_tenant",         columnList = "tenant_id"),
                @Index(name = "idx_att_actor",          columnList = "actor_id, actor_type"),
                @Index(name = "idx_att_date",           columnList = "date"),
                @Index(name = "idx_att_status",         columnList = "status"),
                @Index(name = "idx_att_tenant_date",    columnList = "tenant_id,date"),
                @Index(name = "idx_att_actor_checkout", columnList = "actor_id, actor_type, check_out_time"),
                @Index(name = "idx_att_device",         columnList = "device_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Attendance extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    // ── Actor identification (Unified) ──────────────────────────────────────

    @Column(name = "actor_id", nullable = false)
    private UUID actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false)
    private AttendanceActorType actorType;

    // ── Temporal fields ──────────────────────────────────────────────────────

    @Column(name = "date", nullable = false)
    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Integer totalDurationMinutes;

    // ── Status & Source ──────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Enumerated(EnumType.STRING)
    private AttendanceSource source;

    // ── Device traceability ──────────────────────────────────────────────────

    @Column(name = "device_id")
    private UUID deviceId;

    private String deviceName;

    private String verificationId;

    @Builder.Default
    private Boolean verified = false;

    // ── Context ──────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    private UUID classId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ── Audit timestamps ─────────────────────────────────────────────────────

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}