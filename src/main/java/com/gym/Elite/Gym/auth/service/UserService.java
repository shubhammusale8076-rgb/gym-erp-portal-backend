package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UpdateUserDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserDetailsDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserListDto;
import com.gym.Elite.Gym.auth.entity.Authority;
import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.auth.helper.UserMapper;
import com.gym.Elite.Gym.auth.repo.AuthorityRepo;
import com.gym.Elite.Gym.auth.repo.UserRepo;
import com.gym.Elite.Gym.internal.dto.OwnerCreationRequest;
import com.gym.Elite.Gym.internal.dto.OwnerResponse;
import com.gym.Elite.Gym.tenants.entity.TenantRef;
import com.gym.Elite.Gym.tenants.service.TenantRefService;
import com.gym.Elite.Gym.utility.PasswordGenerator;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        List<User> userList = userRepo.findByTenantId(tenantId);
        return userList.stream().map(userMapper::toListDto).toList();
    }

    public ResponseDto updateUser(UUID userId, UpdateUserDto dto) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields safely
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getEnabled() != null) {
            user.setEnabled(dto.getEnabled());
        }

        // Update role (authority)
        if (dto.getAuthority() != null) {
            Authority authority = authorityRepo
                    .findByRoleCode(dto.getAuthority());

            user.setAuthority(authority);
        }

        userRepo.save(user);

        return ResponseDto.builder().code(200).message("User Updated Successfully").build();
    }

    public ResponseDto deleteUser(UUID userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepo.delete(user);

        return ResponseDto.builder().code(200).message("User Deleted Successfully").build();

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
        TenantRef tenantRef= tenantRefService.validateActiveTenant(request.getTenantId());

        // Step 2 – Resolve or auto-create OWNER authority
        Authority authority = authorityRepo.findByRoleCodeAndTenantId("OWNER", tenantRef.getTenantId());
        if (authority == null) {
            authority = authorityRepo.save(Authority.builder()
                    .roleCode("OWNER")
                    .roleDescription("Gym owner with complete business control and reports")
                    .tenantId(tenantRef.getTenantId())
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
                .tenantId(tenantRef.getTenantId())
                .authority(authority)
                .enabled(true)
                .build();

        userRepo.save(user);

        return OwnerResponse.builder()
                .email(user.getEmail())
                .password(rawPassword)
                .build();
    }
}
