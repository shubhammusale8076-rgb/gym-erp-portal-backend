package com.gym.Elite.Gym.auth.dto.subscriptionDto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class SubscriptionResponseDTO {

    private UUID id;
    private String memberName;
    private String planName;
    private Date startDate;
    private Date endDate;
    private Boolean active;
    private String status;
    private Boolean autoRenew;
    private Integer remainingSessions;
}
