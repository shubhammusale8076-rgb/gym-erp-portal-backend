package com.gym.Elite.Gym.auth.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.memberDto.*;
import com.gym.Elite.Gym.auth.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/create-member")
    @PreAuthorize("hasAuthority('CREATE_MEMBER')")
    public ResponseEntity<MemberCreationResponseDto> createMember(@RequestBody MemberRequestDTO request) {
        MemberCreationResponseDto responseDto = memberService.createMember( request);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/get-member/{memberId}")
    public ResponseEntity<MemberDetailResponseDTO> getMember(@PathVariable UUID memberId) {
        return ResponseEntity.ok(memberService.getMemberById(memberId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponseDto>> searchUsers(@RequestParam String query) {

        return ResponseEntity.ok(memberService.searchUsers(query)
        );
    }

    @GetMapping("/by-tenant")
    public ResponseEntity<List<MemberResponseDTO>> getMembersByTenant() {

        return ResponseEntity.ok(memberService.getMembersByTenant());
    }

    @PutMapping("/update-member/{memberId}")
    @PreAuthorize("hasAuthority('UPDATE_MEMBER')")
    public ResponseEntity<ResponseDto> updateMember(@PathVariable UUID memberId, @RequestBody MemberRequestDTO request) {

        return ResponseEntity.ok(memberService.updateMember(memberId, request));
    }

    @DeleteMapping("/delete-member/{memberId}")
    @PreAuthorize("hasAuthority('DELETE_MEMBER')")
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

    @PostMapping("/assign-role")
    public ResponseEntity<ResponseDto> assignRoleToUsers(@Valid @RequestBody AssignUsersRoleRequestDto request) {

        ResponseDto responseDto = memberService.assignRoleToUsers(request);

        return ResponseEntity.ok(responseDto);
    }

}
