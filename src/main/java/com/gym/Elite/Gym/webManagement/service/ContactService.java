package com.gym.Elite.Gym.webManagement.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.tenants.repo.TenantRefRepository;
import com.gym.Elite.Gym.config.ResourceNotFoundException;
import com.gym.Elite.Gym.utility.SecurityUtils;
import com.gym.Elite.Gym.webManagement.dto.contactUsDto.ContactPageDTO;
import com.gym.Elite.Gym.webManagement.dto.contactUsDto.OperatingHoursDto;
import com.gym.Elite.Gym.webManagement.entity.ContactInfo;
import com.gym.Elite.Gym.webManagement.entity.OperatingHours;
import com.gym.Elite.Gym.webManagement.mapper.ContactPageMapper;
import com.gym.Elite.Gym.webManagement.repo.ContactInfoRepo;
import com.gym.Elite.Gym.webManagement.repo.OperatingHoursRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactInfoRepo contactRepo;
    private final OperatingHoursRepo hoursRepo;
    private final ContactPageMapper mapper;
    private final TenantRefRepository tenantRefRepository;

    // ✅ GET PUBLISHED
    public ContactPageDTO getPublished() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        ContactInfo contact = contactRepo
                .findByTenantIdAndStatus(tenantId, "PUBLISHED")
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        List<OperatingHours> hours = hoursRepo
                .findByTenantIdAndStatus(tenantId, "PUBLISHED");

        return mapper.mapToContactDTO(contact, hours);
    }

    // ✅ GET DRAFT
    public ContactPageDTO getDraft() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        ContactInfo contact = contactRepo
                .findByTenantIdAndStatus(tenantId, "DRAFT")
                .orElse(null);

        List<OperatingHours> hours = hoursRepo
                .findByTenantIdAndStatus(tenantId, "DRAFT");

        return mapper.mapToContactDTO(contact, hours);
    }

    // 🟡 SAVE DRAFT
    public ResponseDto saveDraft(ContactPageDTO dto) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        tenantRefRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        ContactInfo contact = contactRepo
                .findByTenantIdAndStatus(tenantId, "DRAFT")
                .orElseGet(() -> createContact(tenantId));

        mapper.mapContact(contact, dto.getContact());
        contact.setStatus("DRAFT");

        contactRepo.save(contact);

        saveOperatingHours(dto.getOperatingHours(), tenantId, "DRAFT");

        return ResponseDto.builder().code(201).message("Contact Details Saved as Draft").build();

    }

    // 🟢 PUBLISH
    public ResponseDto publish(ContactPageDTO dto) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        tenantRefRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        ContactInfo contact = contactRepo
                .findByTenantIdAndStatus(tenantId, "PUBLISHED")
                .orElseGet(() -> createContact(tenantId));

        mapper.mapContact(contact, dto.getContact());
        contact.setStatus("PUBLISHED");

        contactRepo.save(contact);

        saveOperatingHours(dto.getOperatingHours(), tenantId, "PUBLISHED");

        return ResponseDto.builder().code(201).message("Contact Details has been Published").build();
    }

    private ContactInfo createContact(UUID tenantId) {
        ContactInfo contact = new ContactInfo();
        contact.setTenantId(tenantId);
        return contact;
    }

    private void saveOperatingHours(List<OperatingHoursDto> list, UUID tenantId, String status) {

        // remove old
        hoursRepo.deleteByTenantIdAndStatus(tenantId, status);

        if (list == null || list.isEmpty()) return;

        List<OperatingHours> entities = list.stream().map(dto -> {

            OperatingHours oh = new OperatingHours();
            oh.setTenantId(tenantId);

            oh.setDayOfWeek(dto.getDayOfWeek());

            if (Boolean.TRUE.equals(dto.getIsClosed())) {
                oh.setIsClosed(true);
            } else {
                oh.setOpenTime(LocalTime.parse(dto.getOpenTime()));
                oh.setCloseTime(LocalTime.parse(dto.getCloseTime()));
                oh.setIsClosed(false);
            }

            oh.setStatus(status);

            return oh;

        }).toList();

        hoursRepo.saveAll(entities);
    }
}