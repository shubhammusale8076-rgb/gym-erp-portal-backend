package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceEventDTO {

    private UUID memberId;
    private String gymId;

    private LocalDateTime eventTime;

    private String source;
    private String deviceId;
}