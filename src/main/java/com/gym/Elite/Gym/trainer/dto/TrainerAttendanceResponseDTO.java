package com.gym.Elite.Gym.trainer.dto;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.trainer.entity.TrainerAttendanceStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TrainerAttendanceResponseDTO {
    private UUID id;
    private UUID trainerId;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private TrainerAttendanceStatus status;
    private AttendanceSource source;
    private String notes;
}
