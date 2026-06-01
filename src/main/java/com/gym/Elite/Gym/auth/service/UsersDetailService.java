package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.entity.CustomUserDetails;
import com.gym.Elite.Gym.auth.entity.GymUser;
import com.gym.Elite.Gym.auth.entity.Permission;
import com.gym.Elite.Gym.auth.entity.Role;
import com.gym.Elite.Gym.auth.repo.GymUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersDetailService implements UserDetailsService {

    private final GymUserRepo gymUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GymUser> matches = gymUserRepo.findAllByEmail(username);
        if (matches.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found with userName " + username);
        }
        if (matches.size() > 1) {
            throw new UsernameNotFoundException(
                    "Multiple accounts found for this email; provide tenantId in login request");
        }
        return gymUserRepo.findByEmailWithRoleAndPermissions(username)
                .map(this::buildUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with userName " + username));
    }

    public UserDetails loadUserByEmail(String email, UUID tenantId) throws UsernameNotFoundException {
        GymUser gymUser = (tenantId != null
                ? gymUserRepo.findByEmailAndTenantIdWithRoleAndPermissions(email, tenantId)
                : gymUserRepo.findByEmailWithRoleAndPermissions(email))
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with userName " + email));

        return buildUserDetails(gymUser);
    }

    private CustomUserDetails buildUserDetails(GymUser gymUser) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Set<String> added = new HashSet<>();

        Role role = gymUser.getRole();
        if (role != null) {
            String roleAuthority = "ROLE_" + role.getRoleCode();
            authorities.add(new SimpleGrantedAuthority(roleAuthority));
            added.add(roleAuthority);

            if (role.getPermissions() != null) {
                for (Permission permission : role.getPermissions()) {
                    String code = permission.getPermissionCode();
                    if (added.add(code)) {
                        authorities.add(new SimpleGrantedAuthority(code));
                    }
                }
            }
        }

        return new CustomUserDetails(
                gymUser.getId(),
                gymUser.getEmail(),
                gymUser.getPassword(),
                gymUser.getTenantId(),
                gymUser.getTokenVersion(),
                authorities,
                gymUser.isEnabled(),
                role != null ? role.getRoleCode() : null
        );
    }
}
