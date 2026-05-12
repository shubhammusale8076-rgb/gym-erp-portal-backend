package com.gym.Elite.Gym.attendanceEvent.dto;


import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEventResponseDto {

    private boolean success;

    private String message;

    private UUID attendanceId;

    private UUID actorId;

    private String actorName;

    private AttendanceActorType actorType;

    private AttendanceStatus status;

    private String eventType;

    private LocalDateTime timestamp;
}
