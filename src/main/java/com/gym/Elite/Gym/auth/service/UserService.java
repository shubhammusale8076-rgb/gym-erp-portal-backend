package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UpdateUserDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserDetailsDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserListDto;
import com.gym.Elite.Gym.auth.entity.GymUser;
import com.gym.Elite.Gym.auth.entity.Role;
import com.gym.Elite.Gym.auth.helper.UserMapper;
import com.gym.Elite.Gym.auth.repo.GymUserRepo;
import com.gym.Elite.Gym.auth.repo.RoleRepo;
import com.gym.Elite.Gym.internal.dto.OwnerCreationRequest;
import com.gym.Elite.Gym.internal.dto.OwnerResponse;
import com.gym.Elite.Gym.tenants.entity.TenantRef;
import com.gym.Elite.Gym.tenants.service.TenantRefService;
import com.gym.Elite.Gym.utility.PasswordGenerator;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final GymUserRepo gymUserRepo;
    private final UserMapper userMapper;
    private final RoleRepo roleRepo;
    private final TenantRefService tenantRefService;
    private final GymUserService gymUserService;

    public UserDetailsDto getUserProfile(UUID id) {
        GymUser user = gymUserRepo.findById(id).orElse(null);
        assert user != null;
        return userMapper.toDto(user);
    }

    public List<UserListDto> getAllUsers() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        List<GymUser> userList = gymUserRepo.findNonMemberAndTrainerUsers(tenantId);
        return userList.stream().map(userMapper::toListDto).toList();
    }

    public ResponseDto updateUser(UUID userId, UpdateUserDto dto) {
        GymUser user = gymUserRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

        if (dto.getAuthority() != null) {
            Role role = roleRepo.findByRoleCodeAndTenantId(
                    dto.getAuthority(),
                    SecurityUtils.getCurrentTenantId());
            user.setRole(role);
        }

        gymUserRepo.save(user);

        return ResponseDto.builder().code(200).message("User Updated Successfully").build();
    }

    public ResponseDto deleteUser(UUID userId) {
        GymUser user = gymUserRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        gymUserRepo.delete(user);

        return ResponseDto.builder().code(200).message("User Deleted Successfully").build();
    }

    public OwnerResponse createOwner(OwnerCreationRequest request) {
        TenantRef tenantRef = tenantRefService.validateActiveTenant(request.getTenantId());

        String rawPassword = PasswordGenerator.generateStrongPassword();

        GymUser user = gymUserService.createGymUser(
                request.getEmail(),
                rawPassword,
                tenantRef.getTenantId(),
                "OWNER",
                request.getName(),
                null,
                true
        );

        return OwnerResponse.builder()
                .email(user.getEmail())
                .password(rawPassword)
                .build();
    }
}
