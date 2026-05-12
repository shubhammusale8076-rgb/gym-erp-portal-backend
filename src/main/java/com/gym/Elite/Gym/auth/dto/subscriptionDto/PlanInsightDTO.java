package com.gym.Elite.Gym.auth.dto.subscriptionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanInsightDTO {
    private String planName;
    private long memberCount;
    private double percentage;
}
