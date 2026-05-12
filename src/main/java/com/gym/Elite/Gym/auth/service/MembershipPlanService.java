package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.membershipPlanDto.MemberShipPlanDto;
import com.gym.Elite.Gym.auth.dto.membershipPlanDto.MembershipPlanRequestDTO;
import com.gym.Elite.Gym.auth.dto.membershipPlanDto.MembershipPlanResponseDTO;
import com.gym.Elite.Gym.auth.entity.MembershipPlan;
import com.gym.Elite.Gym.auth.mapper.MemberShipMapper;
import com.gym.Elite.Gym.auth.repo.MembershipPlanRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepo membershipPlanRepo;
    private final MemberShipMapper membershipMapper;

    public ResponseDto createPlan( MembershipPlanRequestDTO request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        MembershipPlan plan = MembershipPlan.builder()
                .name(request.getName())
                .price(request.getPrice())
                .durationInDays(request.getDurationInDays())
                .sessionLimit(request.getSessionLimit())
                .personalTrainerIncluded(request.getPersonalTrainerIncluded())
                .dietPlanIncluded(request.getDietPlanIncluded())
                .discount(request.getDiscount())
                .features(request.getFeatures())
                .active(true)
                .tenantId(tenantId)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        membershipPlanRepo.save(plan);

        return ResponseDto.builder().code(201).message("Membership Plan Added Successfully").build();
    }

    public List<MembershipPlanResponseDTO> getPlansByTenant() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        return membershipPlanRepo.findByTenantId(tenantId)
                .stream()
                .sorted(Comparator.comparing(MembershipPlan::getPrice))
                .map(membershipMapper::mapToPlanDTO)
                .collect(Collectors.toList());
    }


    public List<MemberShipPlanDto> getPlansListByTenant() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        return membershipPlanRepo.findByTenantId(tenantId)
                .stream()
                .sorted(Comparator.comparing(MembershipPlan::getPrice))
                .map(membershipMapper::mapToPlanListDTO)
                .collect(Collectors.toList());
    }

    public MembershipPlanResponseDTO getPlanById(UUID planId) {
        MembershipPlan plan = membershipPlanRepo.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        return membershipMapper.mapToPlanDTO(plan);
    }

    public ResponseDto updatePlan(UUID planId, MembershipPlanRequestDTO request) {

        MembershipPlan plan = membershipPlanRepo.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        plan.setName(request.getName());
        plan.setPrice(request.getPrice());
        plan.setDurationInDays(request.getDurationInDays());
        plan.setSessionLimit(request.getSessionLimit());
        plan.setPersonalTrainerIncluded(request.getPersonalTrainerIncluded());
        plan.setDietPlanIncluded(request.getDietPlanIncluded());
        plan.setBadge(request.getBadge());
        plan.setIsPopular(request.getIsPopular());
        plan.setDiscount(request.getDiscount());
        plan.setActive(request.getIsActive());
        plan.setFeatures(request.getFeatures());

        membershipPlanRepo.save(plan);
        return ResponseDto.builder().code(200).message("Plan Updated Successfully").build();
    }

    public ResponseDto deletePlan(UUID planId) {
        membershipPlanRepo.deleteById(planId);
        return ResponseDto.builder().code(200).message("Membership Plan Deleted Successfully").build();
    }

    public ResponseDto activatePlan(UUID planId) {
        MembershipPlan plan = getEntity(planId);
        plan.setActive(true);
        membershipPlanRepo.save(plan);
        return ResponseDto.builder().code(200).message("Membership Plan Activated Successfully").build();
    }

    public ResponseDto deactivatePlan(UUID planId) {
        MembershipPlan plan = getEntity(planId);
        plan.setActive(false);
        membershipPlanRepo.save(plan);
        return ResponseDto.builder().code(200).message("Membership Plan De-Activated Successfully").build();
    }

    private MembershipPlan getEntity(UUID id) {
        return membershipPlanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
    }

}
