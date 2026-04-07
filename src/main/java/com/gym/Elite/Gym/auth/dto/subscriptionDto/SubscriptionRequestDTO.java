package com.gym.Elite.Gym.auth.dto.subscriptionDto;

import lombok.Data;

import java.util.UUID;

@Data
public class SubscriptionRequestDTO {

    private UUID memberId;
    private UUID planId;
    private Boolean autoRenew;
}
