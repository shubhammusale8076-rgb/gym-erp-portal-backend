package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.memberDto.MemberRequestDTO;
import com.gym.Elite.Gym.auth.dto.memberDto.MemberResponseDTO;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.mapper.MemberMapper;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.integration.client.IntegrationClient;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepo memberRepo;
    private final TenantRepo tenantRepo;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final IntegrationClient integrationClient;

    public ResponseDto createMember(UUID tenantId, MemberRequestDTO request) {

        if (memberRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Member with this email already exists");
        }

        Tenants tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Member member = Member.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdOn(new Date())
                .active(true)
                .tenant(tenant)
                .build();

        memberRepo.save(member);

        // 🚀 Trigger event
        integrationClient.sendEvent("MEMBERSHIP_CREATED", tenantId.toString(), member);

        return ResponseDto.builder().code(201).message("Member ").build();
    }

    public MemberResponseDTO getMemberById(UUID memberId) {
        return memberMapper.mapToDTO(getEntity(memberId));
    }

    public List<MemberResponseDTO> getMembersByTenant(UUID tenantId) {
        return memberRepo.findByTenantId(tenantId)
                .stream()
                .map(memberMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public ResponseDto updateMember(UUID memberId, MemberRequestDTO request) {

        Member member = getEntity(memberId);

        member.setFullName(request.getFullName());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setEmail(request.getEmail());
        member.setUpdatedOn(new Date());

        memberRepo.save(member);

        return ResponseDto.builder().code(200).message("Member Updated Successfully").build();
    }

    public ResponseDto deleteMember(UUID memberId) {
        memberRepo.deleteById(memberId);
        return ResponseDto.builder().code(200).message("Member Deleted Successfully").build();
    }

    public ResponseDto activateMember(UUID memberId) {
        Member member = getEntity(memberId);
        member.setActive(true);
        memberRepo.save(member);
        return ResponseDto.builder().code(200).message("Member Activated Successfully").build();

    }

    public ResponseDto deactivateMember(UUID memberId) {
        Member member = getEntity(memberId);
        member.setActive(false);
        memberRepo.save(member);
        return ResponseDto.builder().code(200).message("Member De-Activated Successfully").build();
    }

    private Member getEntity(UUID id) {
        return memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }
}
