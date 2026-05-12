package com.gym.Elite.Gym.auth.mapper;

import com.gym.Elite.Gym.auth.dto.membershipPlanDto.MemberShipPlanDto;
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
                .period(mapPeriod(plan.getDurationInDays()))
                .badge(plan.getBadge())
                .theme(plan.getName().toLowerCase())
                .isPopular(plan.getIsPopular())
                .discount(plan.getDiscount())
                .active(plan.getActive())
                .features(plan.getFeatures())
                .build();
    }

    public MemberShipPlanDto mapToPlanListDTO(MembershipPlan plan) {

        return MemberShipPlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .price(plan.getPrice())
                .period(mapPeriod(plan.getDurationInDays()))
                .build();
    }

    private String mapPeriod(int days) {
        if (days == 30) return "MONTH";
        if (days == 365) return "YEAR";
        return "CUSTOM";
    }



}
