package com.gym.Elite.Gym.trainer.dto;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import lombok.Data;

@Data
public class TrainerAttendanceRequestDTO {
    private AttendanceSource source;
    private String notes;
}
