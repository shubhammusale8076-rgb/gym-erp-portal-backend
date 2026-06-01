package com.gym.Elite.Gym.auth.dto.membershipPlanDto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PlanDTO {

    private UUID id;
    private String name;
    private String description;
    private String status;
    private String badge; // ELITE TIER

    private Double price;
    private Integer durationInDays;

    private List<String> features;
}
