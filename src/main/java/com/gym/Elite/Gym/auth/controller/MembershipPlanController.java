package com.gym.Elite.Gym.auth.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.membershipPlanDto.MembershipPlanRequestDTO;
import com.gym.Elite.Gym.auth.dto.membershipPlanDto.MembershipPlanResponseDTO;
import com.gym.Elite.Gym.auth.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/membership-plans")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService planService;


    @PostMapping("/create-plan/{tenantId}")
    public ResponseEntity<ResponseDto> createPlan(@PathVariable UUID tenantId, @RequestBody MembershipPlanRequestDTO request) {
        ResponseDto responseDto = planService.createPlan(tenantId, request);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/get-plans/tenant/{tenantId}")
    public ResponseEntity<List<MembershipPlanResponseDTO>> getPlansByTenant(@PathVariable UUID tenantId) {

        return ResponseEntity.ok(planService.getPlansByTenant(tenantId));
    }

    @GetMapping("/get-plan/{planId}")
    public ResponseEntity<MembershipPlanResponseDTO> getPlan(@PathVariable UUID planId) {

        return ResponseEntity.ok(planService.getPlanById(planId));
    }

    @PutMapping("/update-plan/{planId}")
    public ResponseEntity<ResponseDto> updatePlan(@PathVariable UUID planId, @RequestBody MembershipPlanRequestDTO request) {

        return ResponseEntity.ok(planService.updatePlan(planId, request));
    }

    @DeleteMapping("/delete-plan/{planId}")
    public ResponseEntity<ResponseDto> deletePlan(@PathVariable UUID planId) {
        ResponseDto responseDto=  planService.deletePlan(planId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/updated-plan/{planId}/activate")
    public ResponseEntity<ResponseDto> activate(@PathVariable UUID planId) {
        ResponseDto responseDto = planService.activatePlan(planId);
        return ResponseEntity.ok(responseDto);
    }
    @PatchMapping("/updated-plan/{planId}/deactivate")
    public ResponseEntity<ResponseDto> deactivate(@PathVariable UUID planId) {
        ResponseDto responseDto = planService.deactivatePlan(planId);
        return ResponseEntity.ok(responseDto);

    }

}
