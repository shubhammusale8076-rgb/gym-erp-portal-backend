package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.entity.CustomUserDetails;
import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.auth.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersDetailService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= userRepo.findByEmail(username);

        if(null == user){
            throw new UsernameNotFoundException("User Not Found with userName "+username);
        }
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getAuthority() != null) {
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + user.getAuthority().getRoleCode())
            );
        }

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getTenantId(),
                user.getTokenVersion(),
                authorities,
                user.isEnabled()
        );
    }
}
