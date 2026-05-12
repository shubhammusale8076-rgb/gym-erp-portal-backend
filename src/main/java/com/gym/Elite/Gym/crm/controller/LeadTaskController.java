package com.gym.Elite.Gym.crm.controller;

import com.gym.Elite.Gym.crm.dto.ApiResponse;
import com.gym.Elite.Gym.crm.dto.TaskCreateRequest;
import com.gym.Elite.Gym.crm.dto.TaskResponseDto;
import com.gym.Elite.Gym.crm.service.LeadTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LeadTaskController {

    private final LeadTaskService taskService;

    // ── POST /api/leads/{leadId}/tasks ────────────────────────────────────────
    @PostMapping("/api/leads/{leadId}/tasks")
    public ResponseEntity<ApiResponse<TaskResponseDto>> createTask(
            @PathVariable UUID leadId,
            @Valid @RequestBody TaskCreateRequest request) {
        TaskResponseDto dto = taskService.createTask(leadId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created", dto));
    }

    // ── GET /api/leads/{leadId}/tasks ─────────────────────────────────────────
    @GetMapping("/api/leads/{leadId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponseDto>>> getTasksForLead(
            @PathVariable UUID leadId) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTasksForLead(leadId)));
    }

    // ── PATCH /api/tasks/{taskId}/complete ────────────────────────────────────
    @PatchMapping("/api/tasks/{taskId}/complete")
    public ResponseEntity<ApiResponse<TaskResponseDto>> completeTask(
            @PathVariable UUID taskId) {
        return ResponseEntity.ok(
                ApiResponse.success("Task completed", taskService.completeTask(taskId)));
    }
}
