package com.gym.Elite.Gym.crm.controller;

import com.gym.Elite.Gym.crm.dto.*;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import com.gym.Elite.Gym.crm.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    // ── POST /api/leads ───────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponseDto>> createLead(
            @Valid @RequestBody LeadCreateRequest request) {
        LeadResponseDto dto = leadService.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lead created successfully", dto));
    }

    // ── GET /api/leads ────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<Page<LeadResponseDto>>> getLeads(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LeadStage stage,
            @RequestParam(required = false) LeadSource source,
            @RequestParam(required = false) UUID assignedTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LeadResponseDto> result = leadService.getLeads(search, stage, source, assignedTo, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ── GET /api/leads/{id} ───────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadDetailsDto>> getLeadById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(leadService.getLeadById(id)));
    }

    // ── PUT /api/leads/{id} ───────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponseDto>> updateLead(
            @PathVariable UUID id,
            @Valid @RequestBody LeadUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Lead updated successfully", leadService.updateLead(id, request)));
    }

    // ── DELETE /api/leads/{id} ────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable UUID id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok(ApiResponse.success("Lead deleted successfully", null));
    }

    // ── PATCH /api/leads/{id}/stage  (Kanban drag-drop) ──────────────────────
    @PatchMapping("/{id}/stage")
    public ResponseEntity<ApiResponse<LeadResponseDto>> updateStage(
            @PathVariable UUID id,
            @Valid @RequestBody StageUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Stage updated", leadService.updateStage(id, request)));
    }

    // ── PATCH /api/leads/{id}/convert ────────────────────────────────────────
    @PatchMapping("/{id}/convert")
    public ResponseEntity<ApiResponse<LeadResponseDto>> convertLead(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success("Lead converted successfully", leadService.convertLead(id)));
    }

    // ── GET /api/leads/kanban ─────────────────────────────────────────────────
    @GetMapping("/kanban")
    public ResponseEntity<ApiResponse<List<LeadKanbanDto>>> getKanbanBoard() {
        return ResponseEntity.ok(ApiResponse.success(leadService.getKanbanBoard()));
    }
}
