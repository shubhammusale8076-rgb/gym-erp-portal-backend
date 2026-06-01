package com.gym.Elite.Gym.auth.config;

import com.gym.Elite.Gym.auth.entity.Role;
import com.gym.Elite.Gym.auth.repo.RoleRepo;
import com.gym.Elite.Gym.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RolePermissionBootstrap {

    private final RoleRepo roleRepo;
    private final RoleService roleService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void ensureExistingRolesHavePermissions() {
        List<Role> roles = roleRepo.findAll();
        roles.forEach(role -> roleService.getUserRole(role.getRoleCode()));
        log.debug("Ensured default permissions for {} tenant roles", roles.size());
    }
}
