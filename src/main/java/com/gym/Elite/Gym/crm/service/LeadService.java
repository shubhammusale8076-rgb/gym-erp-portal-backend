package com.gym.Elite.Gym.crm.service;

import com.gym.Elite.Gym.crm.dto.*;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LeadService {

    LeadResponseDto createLead(LeadCreateRequest request);

    Page<LeadResponseDto> getLeads(String search, LeadStage stage, LeadSource source,
                                    UUID assignedTo, Pageable pageable);

    LeadDetailsDto getLeadById(UUID id);

    LeadResponseDto updateLead(UUID id, LeadUpdateRequest request);

    void deleteLead(UUID id);

    LeadResponseDto updateStage(UUID id, StageUpdateRequest request);

    LeadResponseDto convertLead(UUID id);

    List<LeadKanbanDto> getKanbanBoard();
}
