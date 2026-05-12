package com.gym.Elite.Gym.crm.controller;

import com.gym.Elite.Gym.crm.dto.ApiResponse;
import com.gym.Elite.Gym.crm.dto.FollowUpCreateRequest;
import com.gym.Elite.Gym.crm.dto.FollowUpResponseDto;
import com.gym.Elite.Gym.crm.service.FollowUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/followups")
@RequiredArgsConstructor
public class FollowUpController {

    private final FollowUpService followUpService;

    // ── POST /api/followups ───────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> createFollowUp(
            @Valid @RequestBody FollowUpCreateRequest request) {
        FollowUpResponseDto dto = followUpService.createFollowUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Follow-up scheduled", dto));
    }

    // ── GET /api/followups/today ──────────────────────────────────────────────
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<FollowUpResponseDto>>> getTodayFollowUps() {
        return ResponseEntity.ok(ApiResponse.success(followUpService.getTodayFollowUps()));
    }

    // ── GET /api/followups/overdue ────────────────────────────────────────────
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<FollowUpResponseDto>>> getOverdueFollowUps() {
        return ResponseEntity.ok(ApiResponse.success(followUpService.getOverdueFollowUps()));
    }

    // ── PATCH /api/followups/{id}/complete ────────────────────────────────────
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> completeFollowUp(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success("Follow-up completed", followUpService.completeFollowUp(id)));
    }
}
