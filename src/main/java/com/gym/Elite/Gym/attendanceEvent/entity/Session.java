package com.gym.Elite.Gym.attendanceEvent.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Session extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private String location;

    private Integer capacity;

    private String trainerName;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
