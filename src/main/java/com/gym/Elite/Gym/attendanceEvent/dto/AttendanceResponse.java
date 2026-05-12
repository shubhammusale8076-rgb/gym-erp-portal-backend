package com.gym.Elite.Gym.attendanceEvent.dto;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus;
import com.gym.Elite.Gym.auth.entity.SessionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Standard response for attendance operations.
 * Unified for all actor types.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private UUID id;
    
    private UUID actorId;
    private AttendanceActorType actorType;
    private String actorName; // Resolved name (Member Name / Trainer Name)

    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer totalDurationMinutes;

    private AttendanceStatus status;
    private AttendanceSource source;

    private UUID deviceId;
    private String deviceName;
    private String verificationId;
    private Boolean verified;

    private SessionType sessionType;
    private UUID classId;

    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
