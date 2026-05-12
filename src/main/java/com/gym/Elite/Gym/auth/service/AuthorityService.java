package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.AuthorityRequestDto;
import com.gym.Elite.Gym.auth.dto.authDtos.AuthorityResponseDto;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Authority;
import com.gym.Elite.Gym.auth.repo.AuthorityRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepo authorityRepository;

    public Authority getUserAuthority(String AuthorityCode, UUID tenantId){

        return authorityRepository.findByRoleCodeAndTenantId(AuthorityCode,tenantId);
    }

    public ResponseDto createAuthority(AuthorityRequestDto authority) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Authority authority1= Authority.builder()
                .roleCode(authority.getRoleCode())
                .roleDescription(authority.getRoleDescription())
                .tenantId(tenantId)
                .build();
         authorityRepository.save(authority1);

        return ResponseDto.builder()
                .code(201)
                .message("Authority created!")
                .build();
    }

    public List<AuthorityResponseDto> getAllAuthorities() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<Object[]> results = authorityRepository.getAuthoritiesWithUserCount(tenantId);

        return results.stream()
                .map(obj -> AuthorityResponseDto.builder()
                        .id((UUID) obj[0])
                        .roleCode((String) obj[1])
                        .roleDescription((String) obj[2])
                        .userCount((Long) obj[3])
                        .build()
                )
                .toList();
    }


}
