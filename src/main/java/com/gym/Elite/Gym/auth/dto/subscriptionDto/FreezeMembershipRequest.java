package com.gym.Elite.Gym.auth.dto.subscriptionDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreezeMembershipRequest {

    private LocalDate freezeStartDate;
    private LocalDate freezeEndDate;
    private String reason;
}
