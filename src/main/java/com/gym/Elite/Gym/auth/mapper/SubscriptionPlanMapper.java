package com.gym.Elite.Gym.auth.mapper;

import com.gym.Elite.Gym.auth.dto.subscriptionDto.SubscriptionResponseDTO;
import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPlanMapper {

    public SubscriptionResponseDTO mapToSubscriptionDTO(MemberSubscription sub) {
        return SubscriptionResponseDTO.builder()
                .id(sub.getId())
                .planName(sub.getPlan().getName())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .status(sub.getStatus() != null ? sub.getStatus().name() : null)
                .active(sub.getActive())
                .remainingSessions(sub.getRemainingSessions())
                .build();
    }
}
