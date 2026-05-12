package com.gym.Elite.Gym.auth.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.subscriptionDto.*;
import com.gym.Elite.Gym.auth.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/create-subscription")
    public ResponseEntity<ResponseDto> createSubscription(@RequestBody SubscriptionRequestDTO request) {

        ResponseDto responseDto =subscriptionService.createSubscription(request);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<SubscriptionResponseDTO> getByMember(@PathVariable UUID memberId) {
        SubscriptionResponseDTO dtoList = subscriptionService.getSubscriptionsByMember(memberId);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ResponseDto> cancel(@PathVariable UUID id) {
        ResponseDto responseDto =  subscriptionService.cancelSubscription(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}/renew")
    public ResponseEntity<ResponseDto> renew(@PathVariable UUID id) {
        ResponseDto responseDto = subscriptionService.renewSubscription(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }

    @GetMapping("/get-insight")
    public ResponseEntity<SubscriptionInsightResponse> getInsights() {

        return ResponseEntity.ok(subscriptionService.getInsights());
    }

    @GetMapping("/memberships")
    public ResponseEntity<List<MembershipTableDTO>> getMemberships() {
        return ResponseEntity.ok(subscriptionService.getMemberships());
    }

    @PostMapping("/{subscriptionId}/freeze")
    public ResponseEntity<ResponseDto> freezeMembership(@PathVariable UUID subscriptionId, @RequestBody FreezeMembershipRequest request) {

        return ResponseEntity.ok(subscriptionService.freezeMembership(subscriptionId, request));
    }

    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<ResponseDto> cancelMembership(@PathVariable UUID subscriptionId, @RequestBody CancelMembershipRequest request) {

        return ResponseEntity.ok(subscriptionService.cancelMembership(subscriptionId, request));
    }

}
