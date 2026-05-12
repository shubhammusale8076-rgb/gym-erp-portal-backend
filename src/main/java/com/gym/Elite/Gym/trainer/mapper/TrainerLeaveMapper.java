package com.gym.Elite.Gym.trainer.mapper;

import com.gym.Elite.Gym.trainer.dto.TrainerLeaveResponseDTO;
import com.gym.Elite.Gym.trainer.entity.TrainerLeave;
import org.springframework.stereotype.Component;

@Component
public class TrainerLeaveMapper {

    public TrainerLeaveResponseDTO toResponseDTO(TrainerLeave leave) {
        if (leave == null) return null;

        TrainerLeaveResponseDTO dto = new TrainerLeaveResponseDTO();
        dto.setId(leave.getId());
        dto.setTrainerId(leave.getTrainerId());
        dto.setLeaveType(leave.getLeaveType());
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus());
        dto.setApprovedBy(leave.getApprovedBy());
        dto.setComments(leave.getComments());
        return dto;
    }
}
