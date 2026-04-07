package com.gym.Elite.Gym.attendanceEvent.entity;

import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenants tenant;

    private String name; // "Power Lifting 101"

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private String location; // Studio A, Main Floor

    private Integer capacity;

    private String trainerName;

    @Enumerated(EnumType.STRING)
    private SessionStatus status; // UPCOMING, ONGOING, COMPLETED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
