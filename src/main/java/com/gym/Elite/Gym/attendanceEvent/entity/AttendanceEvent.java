package com.gym.Elite.Gym.attendanceEvent.entity;

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
 * Raw device event received from any attendance source.
 * Generalised to support multiple actor types.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(
        name = "attendance_events",
        indexes = {
                @Index(name = "idx_evt_tenant",     columnList = "tenant_id"),
                @Index(name = "idx_evt_actor",      columnList = "actor_id, actor_type"),
                @Index(name = "idx_evt_time",       columnList = "event_time"),
                @Index(name = "idx_evt_processed",  columnList = "processed")
        }
)
public class AttendanceEvent extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "actor_id", nullable = false)
    private UUID actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false)
    private AttendanceActorType actorType;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceSource source;

    private String deviceId;

    private String verificationId;

    @Builder.Default
    private boolean processed = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
