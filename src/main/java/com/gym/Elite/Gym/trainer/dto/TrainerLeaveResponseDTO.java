package com.gym.Elite.Gym.trainer.dto;

import com.gym.Elite.Gym.trainer.entity.LeaveStatus;
import com.gym.Elite.Gym.trainer.entity.LeaveType;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TrainerLeaveResponseDTO {
    private UUID id;
    private UUID trainerId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private UUID approvedBy;
    private String comments;
}
