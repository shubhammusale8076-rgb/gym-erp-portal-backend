package com.gym.Elite.Gym.crm.service;

import com.gym.Elite.Gym.crm.dto.NoteCreateRequest;
import com.gym.Elite.Gym.crm.dto.NoteResponseDto;
import com.gym.Elite.Gym.crm.entity.Lead;
import com.gym.Elite.Gym.crm.entity.LeadNote;
import com.gym.Elite.Gym.crm.enums.ActivityType;
import com.gym.Elite.Gym.crm.exception.LeadNotFoundException;
import com.gym.Elite.Gym.crm.repository.LeadNoteRepository;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LeadNoteService {

    private final LeadNoteRepository noteRepository;
    private final LeadRepository     leadRepository;
    private final LeadServiceImpl    leadService;

    public NoteResponseDto addNote(UUID leadId, NoteCreateRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Lead lead = leadRepository.findByIdAndTenantIdAndDeletedFalse(leadId, tenantId)
                .orElseThrow(() -> new LeadNotFoundException("Lead not found: " + leadId));

        LeadNote note = LeadNote.builder()
                .tenantId(tenantId)
                .lead(lead)
                .note(request.getNote())
                .createdBy(getCurrentUsername())
                .build();

        note = noteRepository.save(note);

        leadService.createActivity(lead, ActivityType.NOTE_ADDED,
                "Note Added",
                "New note added by " + getCurrentUsername());

        return toDto(note);
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getNotes(UUID leadId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return noteRepository.findByLeadIdAndTenantIdOrderByCreatedAtDesc(leadId, tenantId)
                .stream().map(this::toDto).toList();
    }

    private NoteResponseDto toDto(LeadNote n) {
        return NoteResponseDto.builder()
                .id(n.getId())
                .leadId(n.getLead().getId())
                .note(n.getNote())
                .createdBy(n.getCreatedBy())
                .createdAt(n.getCreatedAt())
                .build();
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
