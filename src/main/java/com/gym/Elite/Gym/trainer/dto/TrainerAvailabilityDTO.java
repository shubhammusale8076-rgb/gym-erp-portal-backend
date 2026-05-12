package com.gym.Elite.Gym.trainer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerAvailabilityDTO {

    private String dayOfWeek;   // MON, TUE
    private String startTime;   // "06:00"
    private String endTime;     // "10:00"
}
