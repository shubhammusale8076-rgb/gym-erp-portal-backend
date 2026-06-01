package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.AuthorityRequestDto;
import com.gym.Elite.Gym.auth.dto.authDtos.AuthorityResponseDto;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Backward-compatible facade for role APIs ({@code /api/auth/addrole}, {@code /get-roles}).
 */
@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final RoleService roleService;

    public List<AuthorityResponseDto> getAllAuthorities() {
        return roleService.getAllRoles();
    }
}
