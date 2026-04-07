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
    private Integer durationInDays;
    private Integer sessionLimit;
    private Boolean personalTrainerIncluded;
    private Boolean dietPlanIncluded;
    private Double discount;
    private Boolean active;
    private List<String> features;
}
