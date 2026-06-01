package com.gym.Elite.Gym.auth.dto.membershipPlanDto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanComparisonResponseDto {

    private List<MemberShipPlanDto> plans;

    private List<PlanComparisonFeatureDto> features;
}
