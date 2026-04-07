package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.entity.Authority;
import com.gym.Elite.Gym.auth.repo.AuthorityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepo authorityRepository;

    public Authority getUserAuthority(String AuthorityCode){

        return authorityRepository.findByRoleCode(AuthorityCode);
    }

    public Authority createAuthority(Authority authority) {
        Authority authority1= Authority.builder()
                .roleCode(authority.getRoleCode())
                .roleDescription(authority.getRoleDescription())
                .build();
        return authorityRepository.save(authority1);
    }

    public List<Authority> getAllAuthorities() {

        return authorityRepository.findAll();
    }
}
