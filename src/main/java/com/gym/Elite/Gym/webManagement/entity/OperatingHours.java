package com.gym.Elite.Gym.webManagement.entity;

import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(
        name = "operating_hours",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "day_of_week", "status"})
)
@Getter
@Setter
public class OperatingHours {

    @Id
    @GeneratedValue
    private UUID id;

    // ✅ Multi-tenant mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenants tenant;

    // 📅 Day Info
    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek; // MONDAY, TUESDAY...

    // ⏰ Time
    private LocalTime openTime;
    private LocalTime closeTime;

    // ❌ Closed flag
    private Boolean isClosed;

    // 🧠 CMS Control
    @Column(nullable = false)
    private String status; // DRAFT / PUBLISHED
}