package com.gym.Elite.Gym.crm.service;

import com.gym.Elite.Gym.crm.dto.ActivityResponseDto;
import com.gym.Elite.Gym.crm.exception.LeadNotFoundException;
import com.gym.Elite.Gym.crm.repository.LeadActivityRepository;
import com.gym.Elite.Gym.crm.repository.LeadRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeadActivityService {

    private final LeadActivityRepository activityRepository;
    private final LeadRepository         leadRepository;

    public List<ActivityResponseDto> getActivitiesForLead(UUID leadId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        // Ensure the lead belongs to this tenant
        if (!leadRepository.findByIdAndTenantIdAndDeletedFalse(leadId, tenantId).isPresent()) {
            throw new LeadNotFoundException("Lead not found: " + leadId);
        }

        return activityRepository.findByLeadIdAndTenantIdOrderByCreatedAtDesc(leadId, tenantId)
                .stream()
                .map(a -> ActivityResponseDto.builder()
                        .id(a.getId())
                        .leadId(a.getLead().getId())
                        .type(a.getType())
                        .title(a.getTitle())
                        .description(a.getDescription())
                        .createdBy(a.getCreatedBy())
                        .createdAt(a.getCreatedAt())
                        .build())
                .toList();
    }
}
