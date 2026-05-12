package com.gym.Elite.Gym.crm.dto;

import com.gym.Elite.Gym.crm.enums.LeadSource;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourcePerformanceDto {

    private LeadSource source;
    private Long totalLeads;
    private Long convertedLeads;
    private Double conversionRate;
    private Double totalRevenue;
}
