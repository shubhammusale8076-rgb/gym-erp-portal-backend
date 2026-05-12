package com.gym.Elite.Gym.trainer.mapper;

import com.gym.Elite.Gym.trainer.dto.TrainerAttendanceResponseDTO;
import com.gym.Elite.Gym.trainer.entity.TrainerAttendance;
import org.springframework.stereotype.Component;

@Component
public class TrainerAttendanceMapper {

    public TrainerAttendanceResponseDTO toResponseDTO(TrainerAttendance attendance) {
        if (attendance == null) return null;

        TrainerAttendanceResponseDTO dto = new TrainerAttendanceResponseDTO();
        dto.setId(attendance.getId());
        dto.setTrainerId(attendance.getTrainerId());
        dto.setDate(attendance.getDate());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setCheckOutTime(attendance.getCheckOutTime());
        dto.setStatus(attendance.getStatus());
        dto.setSource(attendance.getSource());
        dto.setNotes(attendance.getNotes());
        return dto;
    }
}
