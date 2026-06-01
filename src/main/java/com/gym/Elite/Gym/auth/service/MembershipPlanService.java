package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.membershipPlanDto.*;
import com.gym.Elite.Gym.auth.entity.MembershipPlan;
import com.gym.Elite.Gym.auth.mapper.MemberShipMapper;
import com.gym.Elite.Gym.auth.repo.MembershipPlanRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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

    public PlanComparisonResponseDto getComparison() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<MembershipPlan> plans = membershipPlanRepo.findByTenantIdAndActiveTrue(tenantId);

        List<MemberShipPlanDto> headers =
                plans.stream()
                        .map(plan ->
                                MemberShipPlanDto.builder()
                                        .id(plan.getId())
                                        .name(plan.getName())
                                        .popular(plan.getIsPopular())
                                        .build()
                        )
                        .toList();

        List<PlanComparisonFeatureDto> comparisonFeatures =
                buildComparisonFeatures(plans);

        return PlanComparisonResponseDto.builder()
                .plans(headers)
                .features(comparisonFeatures)
                .build();
    }

    private List<PlanComparisonFeatureDto> buildComparisonFeatures(List<MembershipPlan> plans) {

        List<PlanComparisonFeatureDto> rows = new ArrayList<>();

        rows.add(buildBooleanRow(
                "Personal Trainer",
                plans,
                MembershipPlan::getPersonalTrainerIncluded
        ));

        rows.add(buildBooleanRow(
                "Diet Plan",
                plans,
                MembershipPlan::getDietPlanIncluded
        ));


        rows.add(buildValueRow(
                "Discount",
                plans,
                plan -> plan.getDiscount() + "%"
        ));

        Set<String> premiumFeatures = Set.of(
                "Group classes access",
                "Priority trainer support",
                "Customized diet plan",
                "Body composition analysis",
                "Steam and shower access",
                "Weekly personal training sessions",
                "2 personal training sessions per month",
                "Monthly body measurement tracking",
                "Unlimited gym access",
                "Unlimited premium gym access"
        );

        Set<String> ignoredFeatures = Set.of(
                "Locker facility",
                "Basic trainer guidance",
                "Cardio and strength training access",
                "Gym access during working hours"
        );



        Set<String> uniqueFeatures = new LinkedHashSet<>();

        for (MembershipPlan plan : plans) {
            uniqueFeatures.addAll(plan.getFeatures());
        }

        for (String feature : uniqueFeatures) {

            if (ignoredFeatures.contains(feature)) {
                continue;
            }

            if (!premiumFeatures.contains(feature)) {
                continue;
            }

            Map<String, Object> values = new LinkedHashMap<>();

            for (MembershipPlan plan : plans) {

                values.put(plan.getName(), plan.getFeatures().contains(feature));
            }


            PlanComparisonFeatureDto row =
                    PlanComparisonFeatureDto.builder()
                            .name(feature)
                            .values(values)
                            .build();

            addIfDifferent(rows, row);
        }

        return rows.stream()
                .limit(10)
                .toList();
    }

    private void addIfDifferent(List<PlanComparisonFeatureDto> rows, PlanComparisonFeatureDto row) {

        if (row == null || row.getValues() == null) {
            return;
        }

        Set<Object> uniqueValues = row.getValues()
                .values()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (uniqueValues.size() > 1) {
            rows.add(row);
        }
    }

    private PlanComparisonFeatureDto buildBooleanRow(String featureName, List<MembershipPlan> plans, java.util.function.Function<MembershipPlan, Boolean> extractor) {

        Map<String, Object> values = new LinkedHashMap<>();

        for (MembershipPlan plan : plans) {
            values.put(plan.getName(), extractor.apply(plan));
        }

        return PlanComparisonFeatureDto.builder()
                .name(featureName)
                .values(values)
                .build();
    }

    private PlanComparisonFeatureDto buildValueRow(String featureName, List<MembershipPlan> plans, java.util.function.Function<MembershipPlan, Object> extractor) {

        Map<String, Object> values = new LinkedHashMap<>();

        for (MembershipPlan plan : plans) {
            values.put(plan.getName(), extractor.apply(plan));
        }

        return PlanComparisonFeatureDto.builder()
                .name(featureName)
                .values(values)
                .build();
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
