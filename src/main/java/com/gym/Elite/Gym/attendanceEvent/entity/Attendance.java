package com.gym.Elite.Gym.attendanceEvent.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;

    private String gymId;

    private LocalDate date;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private Integer totalDurationMinutes;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenants tenant;
}