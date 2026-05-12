package com.gym.Elite.Gym.crm.controller;

import com.gym.Elite.Gym.crm.dto.ActivityResponseDto;
import com.gym.Elite.Gym.crm.dto.ApiResponse;
import com.gym.Elite.Gym.crm.service.LeadActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads/{leadId}/activities")
@RequiredArgsConstructor
public class LeadActivityController {

    private final LeadActivityService activityService;

    // ── GET /api/leads/{leadId}/activities ────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityResponseDto>>> getActivities(
            @PathVariable UUID leadId) {
        return ResponseEntity.ok(
                ApiResponse.success(activityService.getActivitiesForLead(leadId)));
    }
}
