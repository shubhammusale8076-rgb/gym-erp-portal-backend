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
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
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
    private final TenantRepo tenantRepo;


    public UserDetailsDto getUserProfile(UUID id) {

        User user = userRepo.findById(id).orElse(null);
        assert user != null;
        return  userMapper.toDto(user);
    }

    public List<UserListDto> getAllUsers() {
        List<User> userList = userRepo.findAll();

        return userList.stream().map(userMapper::toListDto).toList();
    }

    public OwnerResponse createOwner(OwnerCreationRequest request) {
        String rawPassword = PasswordGenerator.generateStrongPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Tenants tenant = tenantRepo.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Authority authority = authorityRepo.findByRoleCode("OWNER");
        if (authority == null) {
            // Handle case where OWNER role is not pre-populated
            authority = authorityRepo.save(Authority.builder()
                    .roleCode("OWNER")
                    .roleDescription("Tenant Owner")
                    .build());
        }

        User user = User.builder()
                .fullName(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .tenant(tenant)
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
