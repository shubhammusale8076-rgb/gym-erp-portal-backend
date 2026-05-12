package com.gym.Elite.Gym.attendanceEvent.dto;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.auth.entity.SessionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * Request body for manual attendance check-in via dashboard.
 * Unified to support Members, Trainers, and Staff.
 */
@Data
public class ManualAttendanceRequest {

    @NotNull(message = "Actor ID is required")
    private UUID actorId;

    @NotNull(message = "Actor Type is required")
    private AttendanceActorType actorType = AttendanceActorType.MEMBER;

    private AttendanceSource source = AttendanceSource.MANUAL;

    private SessionType sessionType = SessionType.GYM;

    private UUID classId;

    private String notes;
}
