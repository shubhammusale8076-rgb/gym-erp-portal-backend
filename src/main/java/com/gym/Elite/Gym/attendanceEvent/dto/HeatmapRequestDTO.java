package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HeatmapRequestDTO {

    private String memberId;

    private String range; // 7D, 30D, 6M

    private LocalDate startDate;
    private LocalDate endDate;
}
