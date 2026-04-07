package com.gym.Elite.Gym.webManagement.service;


import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
import com.gym.Elite.Gym.webManagement.dto.testimonialDTO.TestimonialDTO;
import com.gym.Elite.Gym.webManagement.dto.testimonialDTO.TestimonialResponseDTO;
import com.gym.Elite.Gym.webManagement.entity.Testimonial;
import com.gym.Elite.Gym.webManagement.mapper.TestimonialMapper;
import com.gym.Elite.Gym.webManagement.repo.TestimonialRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TestimonialService {

    private final TestimonialRepo repo;
    private final TenantRepo tenantRepo;
    private final TestimonialMapper testimonialMapper;

    public ResponseDto create(TestimonialDTO dto) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Tenants tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Testimonial t = new Testimonial();

        t.setTenant(tenant);
        t.setClientName(dto.getClientName());
        t.setRole(dto.getRole());
        t.setProfileImageUrl(dto.getProfileImageUrl());
        t.setRating(dto.getRating());
        t.setReview(dto.getReview());

        t.setSource(dto.getSource());
        t.setApproved(true); // admin created → auto approved
        t.setPublished(Boolean.TRUE.equals(dto.getPublishNow()));
        t.setFeatured(Boolean.TRUE.equals(dto.getFeatured()));

        t.setCreatedAt(LocalDateTime.now());
        t.setUpdatedAt(LocalDateTime.now());

        repo.save(t);

        return ResponseDto.builder().code(201).message("Feedback Submitted Successfully").build();
    }

    public List<TestimonialResponseDTO> getAll() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        return repo.findByTenant_Id(tenantId)
                .stream()
                .map(testimonialMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public ResponseDto publish(UUID id) {
        Testimonial t = getById(id);

        if (!t.isApproved()) {
            throw new RuntimeException("Approve before publishing");
        }

        t.setPublished(true);
        t.setUpdatedAt(LocalDateTime.now());

        repo.save(t);

        return ResponseDto.builder().code(201).message("Feedback Published Successfully").build();

    }

    public ResponseDto unpublish(UUID id) {
        Testimonial t = getById(id);

        t.setPublished(false);
        t.setUpdatedAt(LocalDateTime.now());

        repo.save(t);
        return ResponseDto.builder().code(201).message("Feedback Un-Published Successfully").build();

    }

    public void toggleFeature(UUID id) {
        Testimonial t = getById(id);

        t.setFeatured(!t.isFeatured());
        t.setUpdatedAt(LocalDateTime.now());

        repo.save(t);
    }

    public List<TestimonialResponseDTO> getPublishedTestimonials() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        return repo.findByTenant_IdAndIsPublishedTrueAndIsApprovedTrue(tenantId)
                .stream()
                .map(testimonialMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    private Testimonial getById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found"));
    }


}
