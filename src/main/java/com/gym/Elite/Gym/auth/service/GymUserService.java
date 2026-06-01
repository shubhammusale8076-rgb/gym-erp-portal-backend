package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.entity.GymUser;
import com.gym.Elite.Gym.auth.entity.Role;
import com.gym.Elite.Gym.auth.repo.GymUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GymUserService {

    private final GymUserRepo gymUserRepo;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public GymUser createGymUser(
            String email,
            String rawPassword,
            UUID tenantId,
            String roleCode,
            String fullName,
            String phoneNumber,
            boolean enabled) {

        String normalizedEmail = email.trim().toLowerCase();

        if (gymUserRepo.existsByEmailAndTenantId(normalizedEmail, tenantId)) {
            throw new IllegalArgumentException("Email already exists for this tenant");
        }

        Role role = roleService.getUserRole(roleCode);

        GymUser gymUser = GymUser.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(rawPassword))
                .tenantId(tenantId)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .role(role)
                .enabled(enabled)
                .build();

        return gymUserRepo.save(gymUser);
    }
}
