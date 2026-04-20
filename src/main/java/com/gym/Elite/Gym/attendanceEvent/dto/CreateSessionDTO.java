package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateSessionDTO {
    private String name;
    private String description;
    private LocalDateTime sessionTime;
    private Integer capacity;
    private UUID trainerId;
}
