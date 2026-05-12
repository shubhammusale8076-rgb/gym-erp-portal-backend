package com.gym.Elite.Gym.auth.dto.subscriptionDto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHistoryDto {

    private String id;
    private String planName;
    private String cycle;
    private String amount;
    private String status;
}
