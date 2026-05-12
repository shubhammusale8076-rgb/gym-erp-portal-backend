package com.gym.Elite.Gym.auth.dto.subscriptionDto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class FreezeMembershipRequest {

    private LocalDate freezeStartDate;
    private LocalDate freezeEndDate;
    private String reason;
}
