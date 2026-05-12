package com.gym.Elite.Gym.crm.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    private Long totalLeads;
    private Long leadsAddedToday;
    private Long overdueFollowUps;
    private Double conversionRate;
    private Double revenuePipeline;

    // Priority breakdown
    private Long hotLeads;
    private Long warmLeads;
    private Long coldLeads;

    // Stage breakdown
    private Long convertedLeads;
    private Long lostLeads;
    private Long newLeads;
    private Long inNegotiation;
    private Long trialScheduled;
}
