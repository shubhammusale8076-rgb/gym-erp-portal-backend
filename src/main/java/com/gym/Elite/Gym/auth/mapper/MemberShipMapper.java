package com.gym.Elite.Gym.auth.mapper;

import com.gym.Elite.Gym.auth.dto.membershipPlanDto.MembershipPlanResponseDTO;
import com.gym.Elite.Gym.auth.entity.MembershipPlan;
import org.springframework.stereotype.Component;

@Component
public class MemberShipMapper {

    public MembershipPlanResponseDTO mapToPlanDTO(MembershipPlan plan) {
        return MembershipPlanResponseDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .price(plan.getPrice())
                .durationInDays(plan.getDurationInDays())
                .sessionLimit(plan.getSessionLimit())
                .personalTrainerIncluded(plan.getPersonalTrainerIncluded())
                .dietPlanIncluded(plan.getDietPlanIncluded())
                .discount(plan.getDiscount())
                .active(plan.getActive())
                .features(plan.getFeatures())
                .build();
    }
}
