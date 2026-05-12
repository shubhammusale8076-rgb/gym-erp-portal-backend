package com.gym.Elite.Gym.trainer.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.trainer.dto.*;
import com.gym.Elite.Gym.trainer.entity.Trainer;
import com.gym.Elite.Gym.trainer.entity.TrainerAvailability;
import com.gym.Elite.Gym.trainer.entity.TrainerMemberAssignment;
import com.gym.Elite.Gym.trainer.mapper.TrainerMapper;
import com.gym.Elite.Gym.trainer.repo.TrainerAvailabilityRepo;
import com.gym.Elite.Gym.trainer.repo.TrainerMemberAssignmentRepo;
import com.gym.Elite.Gym.trainer.repo.TrainerRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
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
public class TrainerService {

    private final MemberRepo memberRepo;
    private final TrainerRepo trainerRepo;
    private final TrainerMapper trainerMapper;
    private final TrainerAvailabilityRepo availabilityRepo;
    private final TrainerMemberAssignmentRepo memberAssignmentRepo;

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

                // Status
                .available(Boolean.TRUE.equals(request.getAvailable()))
                .visibleOnWebsite(Boolean.TRUE.equals(request.getVisibleOnWebsite()))
                .featured(Boolean.TRUE.equals(request.getFeatured()))
                .active(true)

                .tenantId(tenantId)
                .build();

        // ✅ SAVE TRAINER FIRST
        trainerRepo.save(trainer);

        // ✅ HANDLE AVAILABILITY
        if (request.getAvailability() != null && !request.getAvailability().isEmpty()) {

            List<TrainerAvailability> availabilityList = request.getAvailability()
                    .stream()
                    .map(a -> TrainerAvailability.builder()
                            .trainer(trainer)
                            .tenantId(tenantId)
                            .dayOfWeek(a.getDayOfWeek())
                            .startTime(a.getStartTime())
                            .endTime(a.getEndTime())
                            .active(true)
                            .build()
                    )
                    .collect(Collectors.toList());

            availabilityRepo.saveAll(availabilityList);
        }

        return ResponseDto.builder()
                .code(201)
                .message("Trainer Created Successfully")
                .build();
    }

    // ✅ GET ALL (ADMIN)
    public List<TrainerListDTO> getAllTrainers(UUID tenantId) {
        List<Trainer> trainers = trainerRepo.findByTenantId(tenantId);

        return trainers.stream()
                .map(trainer -> TrainerListDTO.builder()
                        .id(trainer.getId())
                        .fullName(trainer.getFullName())
                        .email(trainer.getEmail())
                        .profileImageUrl(trainer.getProfileImageUrl())
                        .skills(trainer.getSkills())
                        .experienceInYears(trainer.getExperienceInYears())
                        .available(trainer.getAvailable())
                        .assignedMembersCount(
                                memberAssignmentRepo
                                        .countByTrainerIdAndTenantIdAndActiveTrue(trainer.getId(), tenantId)
                        )
                        .build()
                )
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

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Trainer trainer = trainerRepo.findByIdAndTenantId(trainerId, tenantId)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        List<TrainerAvailabilityDTO> availabilityList =
                availabilityRepo.findByTrainerIdAndActiveTrue(trainerId)
                        .stream()
                        .map(a -> TrainerAvailabilityDTO.builder()
                                .dayOfWeek(a.getDayOfWeek())
                                .startTime(a.getStartTime())
                                .endTime(a.getEndTime())
                                .build())
                        .toList();

        List<TrainerMemberDTO> assignedMembers =
                memberAssignmentRepo.findByTrainerIdAndActiveTrue(trainerId)
                        .stream()
                        .map(a -> {
                            Member member = a.getMember();

                            return TrainerMemberDTO.builder()
                                    .id(member.getId())
                                    .fullName(member.getFullName())
                                    .email(member.getEmail())

                                    // 🔥 TEMP (replace later when session module is ready)
                                    .programName("Elite Program")
                                    .nextSession(LocalDateTime.now().plusDays(1))
                                    .progressStatus("ON_TRACK")

                                    .build();
                        })
                        .toList();

        return TrainerResponseDTO.builder()
                .id(trainer.getId())

                // BASIC
                .fullName(trainer.getFullName())
                .email(trainer.getEmail())
                .phoneNumber(trainer.getPhoneNumber())
                .experienceInYears(trainer.getExperienceInYears())

                // PROFILE
                .bio(trainer.getBio())
                .profileImageUrl(trainer.getProfileImageUrl())
                .skills(trainer.getSkills())
                .certifications(trainer.getCertifications())

                // SOCIAL
                .instagramHandle(trainer.getInstagramHandle())
                .linkedinUrl(trainer.getLinkedinUrl())

                // AVAILABILITY
                .availability(availabilityList)

                // STATUS
                .available(trainer.getAvailable())
                .active(trainer.getActive())
                .visibleOnWebsite(trainer.getVisibleOnWebsite())
                .featured(trainer.getFeatured())

                // 🔥 KPI (TEMP values for now)
                .memberSatisfaction(4.8)
                .sessionsCompleted(120)
                .retentionRate(92.0)
                .currentRosterCount(assignedMembers.size())

                // 🔥 ROSTER
                .assignedMembers(assignedMembers)

                .build();
    }

    // ✅ UPDATE
    public ResponseDto updateTrainer(UUID trainerId, TrainerRequestDTO request) {

        Trainer trainer = getEntity(trainerId);

        // ---------------- BASIC ----------------
        trainer.setFullName(request.getFullName());
        trainer.setEmail(request.getEmail());
        trainer.setPhoneNumber(request.getPhoneNumber());
        trainer.setExperienceInYears(request.getExperienceInYears());

        // ---------------- WEBSITE ----------------
        trainer.setBio(request.getBio());
        trainer.setProfileImageUrl(request.getProfileImageUrl());
        trainer.setSkills(request.getSkills());
        trainer.setCertifications(request.getCertifications());

        // ---------------- SOCIAL ----------------
        trainer.setInstagramHandle(request.getInstagramHandle());
        trainer.setLinkedinUrl(request.getLinkedinUrl());

        // ---------------- STATUS ----------------
        trainer.setAvailable(Boolean.TRUE.equals(request.getAvailable()));
        trainer.setVisibleOnWebsite(Boolean.TRUE.equals(request.getVisibleOnWebsite()));
        trainer.setFeatured(Boolean.TRUE.equals(request.getFeatured()));

        trainerRepo.save(trainer);

        // ================= AVAILABILITY =================

        // ❌ STEP 1: Remove old availability
        availabilityRepo.deleteByTrainerId(trainerId);

        // ✅ STEP 2: Add new availability
        if (request.getAvailability() != null && !request.getAvailability().isEmpty()) {

            List<TrainerAvailability> availabilityList = request.getAvailability()
                    .stream()
                    .map(a -> TrainerAvailability.builder()
                            .trainer(trainer)
                            .tenantId(trainer.getTenantId())
                            .dayOfWeek(a.getDayOfWeek())
                            .startTime(a.getStartTime())
                            .endTime(a.getEndTime())
                            .active(true)
                            .build()
                    )
                    .collect(Collectors.toList());

            availabilityRepo.saveAll(availabilityList);
        }

        return ResponseDto.builder()
                .code(200)
                .message("Trainer Updated Successfully")
                .build();
    }

    // ✅ DELETE
    @Transactional
    public ResponseDto deleteTrainer(UUID trainerId) {

        Trainer trainer = getEntity(trainerId);

        // 🔥 Step 1: Delete new availability (TrainerAvailability entity)
        availabilityRepo.deleteByTrainerId(trainerId);


        // 🔥 Step 3: Delete trainer
        trainerRepo.delete(trainer);

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
