package com.gym.Elite.Gym.auth.dto.membershipPlanDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipPlanRequestDTO {

    private String name;
    private double price;
    private Integer durationInDays;
    private Integer sessionLimit;
    private Boolean personalTrainerIncluded;
    private Boolean dietPlanIncluded;
    private String badge;
    private Boolean isPopular;
    private Double discount;
    private Boolean isActive;
    private List<String> features;
}