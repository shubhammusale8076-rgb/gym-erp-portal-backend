package com.gym.Elite.Gym.trainer.entity;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "trainer_attendance")
public class TrainerAttendance extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "trainer_id", nullable = false)
    private UUID trainerId;

    @Column(nullable = false)
    private LocalDate date;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    private TrainerAttendanceStatus status;

    @Enumerated(EnumType.STRING)
    private AttendanceSource source;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
