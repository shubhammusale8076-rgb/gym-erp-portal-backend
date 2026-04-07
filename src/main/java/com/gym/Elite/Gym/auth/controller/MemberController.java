package com.gym.Elite.Gym.auth.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.memberDto.MemberRequestDTO;
import com.gym.Elite.Gym.auth.dto.memberDto.MemberResponseDTO;
import com.gym.Elite.Gym.auth.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/create-member/{tenantId}")
    public ResponseEntity<ResponseDto> createMember(@PathVariable UUID tenantId, @RequestBody MemberRequestDTO request) {
        ResponseDto responseDto = memberService.createMember(tenantId, request);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/get-member/{memberId}")
    public ResponseEntity<MemberResponseDTO> getMember(@PathVariable UUID memberId) {
        return ResponseEntity.ok(memberService.getMemberById(memberId));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<MemberResponseDTO>> getMembersByTenant(@PathVariable UUID tenantId) {

        return ResponseEntity.ok(memberService.getMembersByTenant(tenantId));
    }

    @PutMapping("/update-member/{memberId}")
    public ResponseEntity<ResponseDto> updateMember(@PathVariable UUID memberId, @RequestBody MemberRequestDTO request) {

        return ResponseEntity.ok(memberService.updateMember(memberId, request));
    }

    @DeleteMapping("/delete-member/{memberId}")
    public ResponseEntity<ResponseDto> deleteMember(@PathVariable UUID memberId) {
        ResponseDto responseDto =  memberService.deleteMember(memberId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{memberId}/activate")
    public ResponseEntity<ResponseDto> activate(@PathVariable UUID memberId) {
        ResponseDto responseDto = memberService.activateMember(memberId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{memberId}/deactivate")
    public ResponseEntity<ResponseDto> deactivate(@PathVariable UUID memberId) {
        ResponseDto responseDto = memberService.deactivateMember(memberId);
        return ResponseEntity.ok(responseDto);
    }
}
