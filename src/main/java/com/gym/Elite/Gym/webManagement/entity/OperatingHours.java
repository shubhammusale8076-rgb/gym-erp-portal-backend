package com.gym.Elite.Gym.webManagement.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(
        name = "operating_hours",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "day_of_week", "status"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OperatingHours extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek; // MONDAY, TUESDAY...

    private LocalTime openTime;
    private LocalTime closeTime;

    private Boolean isClosed;

    @Column(nullable = false)
    private String status; // DRAFT / PUBLISHED
}