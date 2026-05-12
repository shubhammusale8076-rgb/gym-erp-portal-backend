package com.gym.Elite.Gym.crm.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionAnalyticsDto {

    private Long totalLeads;
    private Long convertedLeads;
    private Long lostLeads;
    private Double conversionRate;       // percentage
    private Double lossRate;             // percentage
    private Double avgDaysToConvert;
    private Double avgLeadScore;
}
