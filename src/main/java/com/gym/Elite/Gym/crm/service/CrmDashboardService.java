package com.gym.Elite.Gym.crm.service;

import com.gym.Elite.Gym.crm.dto.ConversionAnalyticsDto;
import com.gym.Elite.Gym.crm.dto.DashboardStatsDto;
import com.gym.Elite.Gym.crm.dto.SourcePerformanceDto;
import com.gym.Elite.Gym.crm.enums.LeadPriority;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import com.gym.Elite.Gym.crm.repository.FollowUpRepository;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrmDashboardService {

    private final LeadRepository    leadRepository;
    private final FollowUpRepository followUpRepository;

    // =========================================================================
    // MAIN DASHBOARD STATS
    // =========================================================================

    public DashboardStatsDto getDashboardStats() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        long totalLeads      = leadRepository.countByTenantIdAndDeletedFalse(tenantId);
        long leadsToday      = leadRepository.countByTenantIdAndDeletedFalseAndCreatedAtAfter(tenantId, startOfToday);
        long overdueFollowUps= followUpRepository.countByTenantIdAndCompletedFalseAndOverdueTrue(tenantId);
        long convertedLeads  = leadRepository.countByTenantIdAndDeletedFalseAndStage(tenantId, LeadStage.CONVERTED);
        long lostLeads       = leadRepository.countByTenantIdAndDeletedFalseAndStage(tenantId, LeadStage.LOST);
        long newLeads        = leadRepository.countByTenantIdAndDeletedFalseAndStage(tenantId, LeadStage.NEW_LEAD);
        long negotiation     = leadRepository.countByTenantIdAndDeletedFalseAndStage(tenantId, LeadStage.NEGOTIATION);
        long trialScheduled  = leadRepository.countByTenantIdAndDeletedFalseAndStage(tenantId, LeadStage.TRIAL_SCHEDULED);
        long hotLeads        = leadRepository.countByTenantIdAndDeletedFalseAndPriority(tenantId, LeadPriority.HOT);
        long warmLeads       = leadRepository.countByTenantIdAndDeletedFalseAndPriority(tenantId, LeadPriority.WARM);
        long coldLeads       = leadRepository.countByTenantIdAndDeletedFalseAndPriority(tenantId, LeadPriority.COLD);

        double conversionRate = totalLeads > 0
                ? Math.round((convertedLeads * 100.0 / totalLeads) * 100.0) / 100.0
                : 0.0;

        Double pipeline = leadRepository.sumRevenuePipelineByTenant(tenantId);

        return DashboardStatsDto.builder()
                .totalLeads(totalLeads)
                .leadsAddedToday(leadsToday)
                .overdueFollowUps(overdueFollowUps)
                .conversionRate(conversionRate)
                .revenuePipeline(pipeline != null ? pipeline : 0.0)
                .hotLeads(hotLeads)
                .warmLeads(warmLeads)
                .coldLeads(coldLeads)
                .convertedLeads(convertedLeads)
                .lostLeads(lostLeads)
                .newLeads(newLeads)
                .inNegotiation(negotiation)
                .trialScheduled(trialScheduled)
                .build();
    }

    // =========================================================================
    // CONVERSION ANALYTICS
    // =========================================================================

    public ConversionAnalyticsDto getConversionAnalytics() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        long total     = leadRepository.countByTenantIdAndDeletedFalse(tenantId);
        long converted = leadRepository.countByTenantIdAndDeletedFalseAndConvertedTrue(tenantId);
        long lost      = leadRepository.countByTenantIdAndDeletedFalseAndStage(tenantId, LeadStage.LOST);
        Double avgScore = leadRepository.findAvgLeadScore(tenantId);

        double convRate = total > 0 ? Math.round((converted * 100.0 / total) * 100.0) / 100.0 : 0.0;
        double lossRate = total > 0 ? Math.round((lost     * 100.0 / total) * 100.0) / 100.0 : 0.0;

        return ConversionAnalyticsDto.builder()
                .totalLeads(total)
                .convertedLeads(converted)
                .lostLeads(lost)
                .conversionRate(convRate)
                .lossRate(lossRate)
                .avgDaysToConvert(0.0)  // Future: calculate from createdAt → convertedAt
                .avgLeadScore(avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0)
                .build();
    }

    // =========================================================================
    // SOURCE PERFORMANCE
    // =========================================================================

    public List<SourcePerformanceDto> getSourcePerformance() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        List<Object[]> rows = leadRepository.findSourcePerformanceByTenant(tenantId);
        List<SourcePerformanceDto> result = new ArrayList<>();

        for (Object[] row : rows) {
            LeadSource source    = (LeadSource) row[0];
            long       total     = ((Number) row[1]).longValue();
            long       converted = ((Number) row[2]).longValue();
            double     revenue   = ((Number) row[3]).doubleValue();

            double rate = total > 0 ? Math.round((converted * 100.0 / total) * 100.0) / 100.0 : 0.0;

            result.add(SourcePerformanceDto.builder()
                    .source(source)
                    .totalLeads(total)
                    .convertedLeads(converted)
                    .conversionRate(rate)
                    .totalRevenue(revenue)
                    .build());
        }
        return result;
    }

    // =========================================================================
    // REVENUE PIPELINE
    // =========================================================================

    public Double getRevenuePipeline() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        Double pipeline = leadRepository.sumRevenuePipelineByTenant(tenantId);
        return pipeline != null ? pipeline : 0.0;
    }
}
