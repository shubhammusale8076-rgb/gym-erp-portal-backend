package com.gym.Elite.Gym.auth.dto.subscriptionDto;

import com.gym.Elite.Gym.auth.dto.memberDto.MemberDto;
import com.gym.Elite.Gym.payment.dto.PaymentMethodDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SubscriptionResponseDTO {

    private MemberDto member;
    private CurrentSubscriptionDto currentSubscription;
    private LoyaltyDto loyalty;
    private PaymentMethodDto paymentMethod;
    private List<SubscriptionHistoryDto> history;
}

