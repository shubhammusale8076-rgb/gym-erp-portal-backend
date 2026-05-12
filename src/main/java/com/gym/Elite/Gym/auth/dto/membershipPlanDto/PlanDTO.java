package com.gym.Elite.Gym.auth.dto.membershipPlanDto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanDTO {

    private String name;
    private String description;
    private String status;
    private String badge; // ELITE TIER

    private Double price;
    private Integer durationInDays;

    private List<String> features;
}
