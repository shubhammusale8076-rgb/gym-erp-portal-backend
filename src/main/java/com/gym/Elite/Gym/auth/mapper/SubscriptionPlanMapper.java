package com.gym.Elite.Gym.auth.mapper;

import com.gym.Elite.Gym.auth.dto.subscriptionDto.SubscriptionResponseDTO;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPlanMapper {

    public SubscriptionResponseDTO mapToSubscriptionDTO(MemberSubscription sub) {
        return SubscriptionResponseDTO.builder()
                .id(sub.getId())
                .memberName(sub.getMember().getFullName())
                .planName(sub.getPlan().getName())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .active(sub.getActive())
                .status(sub.getStatus())
                .autoRenew(sub.getAutoRenew())
                .build();
    }


}
