package com.gym.Elite.Gym.auth.dto.subscriptionDto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyDto {

    private String status;
    private String memberSince;
    private String stats;
    private Integer progress;
    private String nextMilestone;
}
