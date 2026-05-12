package com.gym.Elite.Gym.auth.dto.subscriptionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionInsightResponse {
    private List<PlanInsightDTO> plans;
    private long totalMembers;
}
