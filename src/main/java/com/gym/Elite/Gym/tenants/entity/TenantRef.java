package com.gym.Elite.Gym.tenants.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "tenant_ref")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantRef {

    /**
     * Matches the tenantId from the Admin Panel — no auto-generation.
     * This is the shared key across the system.
     */
    @Id
    @Column(nullable = false, updatable = false)
    private UUID tenantId;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TenantStatus status = TenantStatus.ACTIVE;

    @Column(nullable = false)
    private String planCode;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public enum TenantStatus {
        ACTIVE, SUSPENDED
    }
}
