package com.gym.Elite.Gym.auth.dto.membershipPlanDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberShipPlanDto {

    private UUID id;
    private String name;
    private double price;
    private String period;
    private Boolean popular;
}
