package com.gym.Elite.Gym.crm.controller;

import com.gym.Elite.Gym.crm.dto.ApiResponse;
import com.gym.Elite.Gym.crm.dto.ConversionAnalyticsDto;
import com.gym.Elite.Gym.crm.dto.DashboardStatsDto;
import com.gym.Elite.Gym.crm.dto.SourcePerformanceDto;
import com.gym.Elite.Gym.crm.service.CrmDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crm")
@RequiredArgsConstructor
public class CrmDashboardController {

    private final CrmDashboardService dashboardService;

    // ── GET /api/crm/dashboard ────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardStats()));
    }

    // ── GET /api/crm/conversion-rate ──────────────────────────────────────────
    @GetMapping("/conversion-rate")
    public ResponseEntity<ApiResponse<ConversionAnalyticsDto>> getConversionRate() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getConversionAnalytics()));
    }

    // ── GET /api/crm/source-performance ──────────────────────────────────────
    @GetMapping("/source-performance")
    public ResponseEntity<ApiResponse<List<SourcePerformanceDto>>> getSourcePerformance() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSourcePerformance()));
    }

    // ── GET /api/crm/revenue-pipeline ─────────────────────────────────────────
    @GetMapping("/revenue-pipeline")
    public ResponseEntity<ApiResponse<Double>> getRevenuePipeline() {
        return ResponseEntity.ok(ApiResponse.success(
                "Revenue pipeline (active leads)",
                dashboardService.getRevenuePipeline()));
    }
}
