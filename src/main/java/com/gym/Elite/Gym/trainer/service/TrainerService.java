package com.gym.Elite.Gym.trainer.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.trainer.dto.TrainerRequestDTO;
import com.gym.Elite.Gym.trainer.dto.TrainerResponseDTO;
import com.gym.Elite.Gym.trainer.entity.Trainer;
import com.gym.Elite.Gym.trainer.mapper.TrainerMapper;
import com.gym.Elite.Gym.trainer.repo.TrainerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerService {

    private final TrainerRepo trainerRepo;
    private final TrainerMapper trainerMapper;

    // ✅ CREATE
    public ResponseDto createTrainer(UUID tenantId, TrainerRequestDTO request) {

        Trainer trainer = Trainer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .experienceInYears(request.getExperienceInYears())

                // Website
                .bio(request.getBio())
                .profileImageUrl(request.getProfileImageUrl())
                .skills(request.getSkills())
                .certifications(request.getCertifications())

                // Social
                .instagramHandle(request.getInstagramHandle())
                .linkedinUrl(request.getLinkedinUrl())

                // Availability
                .availableDays(request.getAvailableDays())
                .morningShiftStart(request.getMorningShiftStart())
                .morningShiftEnd(request.getMorningShiftEnd())
                .eveningShiftStart(request.getEveningShiftStart())
                .eveningShiftEnd(request.getEveningShiftEnd())

                // Status
                .available(Boolean.TRUE.equals(request.getAvailable()))
                .visibleOnWebsite(Boolean.TRUE.equals(request.getVisibleOnWebsite()))
                .featured(Boolean.TRUE.equals(request.getFeatured()))
                .active(true)

                .createdOn(new Date())
                .tenantId(tenantId)
                .build();

        trainerRepo.save(trainer);

        return ResponseDto.builder()
                .code(201)
                .message("Trainer Created Successfully")
                .build();
    }

    // ✅ GET ALL (ADMIN)
    public List<TrainerResponseDTO> getAllTrainers(UUID tenantId) {
        return trainerRepo.findByTenantId(tenantId)
                .stream()
                .map(trainerMapper::mapToTrainerDTO)
                .collect(Collectors.toList());
    }

    // ✅ WEBSITE
    public List<TrainerResponseDTO> getWebsiteTrainers(UUID tenantId) {
        return trainerRepo.findByTenantIdAndVisibleOnWebsiteTrueAndActiveTrue(tenantId)
                .stream()
                .map(trainerMapper::mapToTrainerDTO)
                .collect(Collectors.toList());
    }

    // ✅ GET BY ID
    public TrainerResponseDTO getTrainerById(UUID trainerId) {
        return trainerMapper.mapToTrainerDTO(getEntity(trainerId));
    }

    // ✅ UPDATE
    public ResponseDto updateTrainer(UUID trainerId, TrainerRequestDTO request) {

        Trainer trainer = getEntity(trainerId);

        trainer.setFullName(request.getFullName());
        trainer.setEmail(request.getEmail());
        trainer.setPhoneNumber(request.getPhoneNumber());
        trainer.setExperienceInYears(request.getExperienceInYears());

        // Website
        trainer.setBio(request.getBio());
        trainer.setProfileImageUrl(request.getProfileImageUrl());
        trainer.setSkills(request.getSkills());
        trainer.setCertifications(request.getCertifications());

        // Social
        trainer.setInstagramHandle(request.getInstagramHandle());
        trainer.setLinkedinUrl(request.getLinkedinUrl());

        // Availability
        trainer.setAvailableDays(request.getAvailableDays());
        trainer.setMorningShiftStart(request.getMorningShiftStart());
        trainer.setMorningShiftEnd(request.getMorningShiftEnd());
        trainer.setEveningShiftStart(request.getEveningShiftStart());
        trainer.setEveningShiftEnd(request.getEveningShiftEnd());

        // Status
        trainer.setAvailable(request.getAvailable());
        trainer.setVisibleOnWebsite(request.getVisibleOnWebsite());
        trainer.setFeatured(request.getFeatured());

        trainer.setUpdatedOn(new Date());

        trainerRepo.save(trainer);

        return ResponseDto.builder()
                .code(200)
                .message("Trainer Updated Successfully")
                .build();
    }

    // ✅ DELETE
    public ResponseDto deleteTrainer(UUID trainerId) {
        trainerRepo.deleteById(trainerId);
        return ResponseDto.builder()
                .code(200)
                .message("Trainer Deleted Successfully")
                .build();
    }

    // ✅ ACTIVATE / DEACTIVATE
    public ResponseDto activateTrainer(UUID trainerId) {
        Trainer trainer = getEntity(trainerId);
        trainer.setActive(true);
        trainerRepo.save(trainer);
        return ResponseDto.builder().code(200).message("Trainer Activated").build();
    }

    public ResponseDto deactivateTrainer(UUID trainerId) {
        Trainer trainer = getEntity(trainerId);
        trainer.setActive(false);
        trainerRepo.save(trainer);
        return ResponseDto.builder().code(200).message("Trainer Deactivated").build();
    }

    // ✅ TOGGLE WEBSITE VISIBILITY
    public ResponseDto toggleVisibility(UUID trainerId, boolean visible) {
        Trainer trainer = getEntity(trainerId);
        trainer.setVisibleOnWebsite(visible);
        trainerRepo.save(trainer);
        return ResponseDto.builder().code(200).message("Visibility Updated").build();
    }

    // ✅ TOGGLE FEATURED
    public ResponseDto toggleFeatured(UUID trainerId) {
        Trainer trainer = getEntity(trainerId);
        trainer.setFeatured(!trainer.getFeatured());
        trainerRepo.save(trainer);
        return ResponseDto.builder().code(200).message("Feature Updated").build();
    }

    // 🔁 COMMON
    private Trainer getEntity(UUID id) {
        return trainerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
    }
}
