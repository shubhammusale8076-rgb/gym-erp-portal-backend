package com.gym.Elite.Gym.crm.controller;

import com.gym.Elite.Gym.crm.dto.ApiResponse;
import com.gym.Elite.Gym.crm.dto.NoteCreateRequest;
import com.gym.Elite.Gym.crm.dto.NoteResponseDto;
import com.gym.Elite.Gym.crm.service.LeadNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads/{leadId}/notes")
@RequiredArgsConstructor
public class LeadNoteController {

    private final LeadNoteService noteService;

    // ── POST /api/leads/{leadId}/notes ────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponseDto>> addNote(
            @PathVariable UUID leadId,
            @Valid @RequestBody NoteCreateRequest request) {
        NoteResponseDto dto = noteService.addNote(leadId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Note added", dto));
    }

    // ── GET /api/leads/{leadId}/notes ─────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoteResponseDto>>> getNotes(
            @PathVariable UUID leadId) {
        return ResponseEntity.ok(ApiResponse.success(noteService.getNotes(leadId)));
    }
}
