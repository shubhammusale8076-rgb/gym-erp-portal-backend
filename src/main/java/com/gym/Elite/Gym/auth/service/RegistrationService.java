package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.RegistrationRequest;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.GymUser;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.repo.GymUserRepo;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.crm.integration.client.WhatsAppIntegrationClient;
import com.gym.Elite.Gym.integration.client.GoogleIntegrationClient;
import com.gym.Elite.Gym.integration.dto.google.GooglePasswordResetRequestDto;
import com.gym.Elite.Gym.utility.PasswordGenerator;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerErrorException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final GymUserService gymUserService;
    private final MemberRepo memberRepo;
    private final PasswordEncoder passwordEncoder;
    private final GymUserRepo gymUserRepo;
    private final GoogleIntegrationClient integrationClient;


    @Transactional
    public ResponseDto createUser(RegistrationRequest request) {
        UUID tenantId = request.getTenantId() != null
                ? request.getTenantId()
                : SecurityUtils.getCurrentTenantId();

        try {
            GymUser savedUser = gymUserService.createGymUser(
                    request.getEmail(),
                    request.getPassword().toString(),
                    tenantId,
                    request.getAuthorityCode(),
                    request.getFullName(),
                    request.getPhoneNumber(),
                    true
            );

            return ResponseDto.builder()
                    .code(201)
                    .message("User created!")
                    .id(savedUser.getId())
                    .password(savedUser.getPassword())
                    .userName(savedUser.getEmail())
                    .build();

        } catch (IllegalArgumentException e) {
            return ResponseDto.builder()
                    .code(400)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new ServerErrorException(e.getMessage(), e.getCause());
        }
    }

    @Transactional
    public ResponseDto adminResetMemberPassword(UUID memberId) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Member member = memberRepo.findByIdAndTenantId(memberId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        GymUser gymUser = member.getGymUser();

        if (gymUser == null) {
            throw new IllegalArgumentException("Gym user not linked with member");
        }

        if (member.getPhoneNumber() == null || member.getPhoneNumber().isBlank()) {

            throw new IllegalArgumentException("Phone number not available");
        }

        String temporaryPassword = PasswordGenerator.generateStrongPassword();
        gymUser.setPassword(passwordEncoder.encode(temporaryPassword));
        gymUser.setPasswordResetRequired(true);
        gymUser.setPasswordUpdatedAt(LocalDateTime.now());

        gymUserRepo.save(gymUser);

        GooglePasswordResetRequestDto request =  GooglePasswordResetRequestDto.builder()
                .tenantId(tenantId)
                .email(member.getEmail())
                .memberName(member.getFullName())
                .temporaryPassword(temporaryPassword)
                .build();

        try{
            ResponseDto responseDto =  integrationClient.sendPasswordResetMessage(request);

            return ResponseDto.builder()
                    .code(200)
                    .message(responseDto.getMessage())
                    .build();
        } catch (Exception ex) {
            log.error(
                    "Failed to send password reset email",
                    ex
            );

            throw new RuntimeException(
                    "Failed to send password reset email"
            );
        }
    }
}
