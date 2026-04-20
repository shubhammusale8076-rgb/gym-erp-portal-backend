package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.RegistrationRequest;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.auth.repo.UserRepo;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final UserRepo userRepo;
    private final TenantRepo tenantRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;

    @Transactional
    public ResponseDto createUser(RegistrationRequest request) {

        User existing = userRepo.findByEmail(request.getEmail());

        if(null != existing){
            return  ResponseDto.builder()
                    .code(400)
                    .message("Email already exist!")
                    .build();
        }
        try{
            // Phase 3 FIX: Assign Tenant during user creation
            Tenants tenant = null;
            if (request.getTenantId() != null) {
                tenant = tenantRepo.findById(request.getTenantId())
                        .orElseThrow(() -> new RuntimeException("Tenant not found"));
            }

            User user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhoneNumber(request.getPhoneNumber());
            user.setTenant(tenant);

            user.setAuthority(authorityService.getUserAuthority(request.getAuthorityCode()));
            User savedUser = userRepo.save(user);

            return ResponseDto.builder()
                    .code(201)
                    .message("User created!")
                    .id(savedUser.getId())
                    .userName(savedUser.getEmail())
                    .build();


        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new ServerErrorException(e.getMessage(),e.getCause());
        }


    }
}
