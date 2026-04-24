package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.userDtos.UserDetailsDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserListDto;
import com.gym.Elite.Gym.auth.entity.Authority;
import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.auth.helper.UserMapper;
import com.gym.Elite.Gym.auth.repo.AuthorityRepo;
import com.gym.Elite.Gym.auth.repo.UserRepo;
import com.gym.Elite.Gym.internal.dto.OwnerCreationRequest;
import com.gym.Elite.Gym.internal.dto.OwnerResponse;
import com.gym.Elite.Gym.tenants.service.TenantRefService;
import com.gym.Elite.Gym.utility.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepo authorityRepo;
    private final TenantRefService tenantRefService;

    public UserDetailsDto getUserProfile(UUID id) {
        User user = userRepo.findById(id).orElse(null);
        assert user != null;
        return userMapper.toDto(user);
    }

    public List<UserListDto> getAllUsers() {
        List<User> userList = userRepo.findAll();
        return userList.stream().map(userMapper::toListDto).toList();
    }

    /**
     * Creates a tenant OWNER user for the given tenantId.
     *
     * Validation flow:
     *  1. TenantRef must exist and be ACTIVE (validated via TenantRefService)
     *  2. OWNER authority is fetched or auto-created
     *  3. A strong password is generated and encoded
     *  4. User is persisted with tenantId String — no Tenants entity needed
     *  5. Raw credentials returned to caller (Admin Panel stores them)
     */
    public OwnerResponse createOwner(OwnerCreationRequest request) {

        // Step 1 – Validate tenant via TenantRef (lightweight registry)
        tenantRefService.validateActiveTenant(request.getTenantId());

        // Step 2 – Resolve or auto-create OWNER authority
        Authority authority = authorityRepo.findByRoleCode("OWNER");
        if (authority == null) {
            authority = authorityRepo.save(Authority.builder()
                    .roleCode("OWNER")
                    .roleDescription("Tenant Owner")
                    .build());
        }

        // Step 3 – Generate credentials
        String rawPassword = PasswordGenerator.generateStrongPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Step 4 – Persist user with plain tenantId String
        User user = User.builder()
                .fullName(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .tenantId(request.getTenantId())
                .authority(authority)
                .enabled(true)
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        userRepo.save(user);

        return OwnerResponse.builder()
                .email(user.getEmail())
                .password(rawPassword)
                .build();
    }
}
