package com.gym.Elite.Gym.attendanceEvent.entity;

import com.gym.Elite.Gym.attendanceEvent.enums.DeviceType;
import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a physical or logical attendance device registered for a tenant.
 *
 * Design principles:
 * - Fully tenant-isolated — no cross-tenant device visibility
 * - Device credentials (apiKey) stored here; never in AttendanceService
 * - Adding a new hardware model = register a new record; zero code change
 * - The adapter layer uses AttendanceSource (not DeviceType) for routing,
 *   giving flexibility to have multiple device models for the same source type
 */
@Entity
@Table(
        name = "attendance_devices",
        indexes = {
                @Index(name = "idx_device_tenant", columnList = "tenant_id"),
                @Index(name = "idx_device_code", columnList = "device_code"),
                @Index(name = "idx_device_tenant_active", columnList = "tenant_id, active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AttendanceDevice extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String deviceName;

    /** Unique code used by the device itself when posting events */
    @Column(name = "device_code", nullable = false)
    private String deviceCode;

    private String manufacturer;

    private String model;

    private String ipAddress;

    private Integer port;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    /** API key sent by device in Authorization header; stored hashed in production */
    @Column(columnDefinition = "TEXT")
    private String apiKey;

    private LocalDateTime lastSyncAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
