package com.gym.Elite.Gym.auth.dto.membershipPlanDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembershipPlanResponseDTO {

    private UUID id;
    private String name;
    private double price;
    private String period;
    private String badge;
    private String theme;
    private Boolean isPopular;
    private Double discount;
    private Boolean active;
    private List<String> features;
}
