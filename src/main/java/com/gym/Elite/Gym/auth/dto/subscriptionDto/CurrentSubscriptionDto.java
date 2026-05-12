package com.gym.Elite.Gym.auth.dto.subscriptionDto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentSubscriptionDto {

    private String id;
    private String planName;
    private String status;
    private String startDate;
    private String endDate;
    private Long remainingDays;
    private String duration;
    private String paymentStatus;
    private Boolean autoRenew;
}
