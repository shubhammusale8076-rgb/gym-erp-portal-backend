package com.gym.Elite.Gym.auth.dto.membershipPlanDto;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanComparisonFeatureDto {

    private String name;

    private Map<String, Object> values;
}