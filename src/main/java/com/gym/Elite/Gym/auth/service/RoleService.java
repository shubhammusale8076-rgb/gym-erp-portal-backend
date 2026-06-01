package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.AuthorityResponseDto;
import com.gym.Elite.Gym.auth.dto.authDtos.PermissionDto;
import com.gym.Elite.Gym.auth.dto.authDtos.PermissionResponseDto;
import com.gym.Elite.Gym.auth.dto.authDtos.RoleUserDto;
import com.gym.Elite.Gym.auth.entity.GymUser;
import com.gym.Elite.Gym.auth.entity.Permission;
import com.gym.Elite.Gym.auth.entity.Role;
import com.gym.Elite.Gym.auth.repo.GymUserRepo;
import com.gym.Elite.Gym.auth.repo.PermissionRepo;
import com.gym.Elite.Gym.auth.repo.RoleRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;
    private final GymUserRepo gymUserRepo;


    public Role getUserRole(String roleCode) {
        return roleRepo.findByRoleCodeAndSystemRoleTrue(roleCode)
                .orElseThrow(()-> new RuntimeException("Role Not Found"));
    }

    public List<AuthorityResponseDto> getAllRoles() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<Role> roles = roleRepo.findAllRolesWithPermissions(tenantId);

        if (roles.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> roleIds = roles.stream()
                .map(Role::getId)
                .toList();

        List<GymUser> users = gymUserRepo.findUsersByRoleIdsAndTenant(roleIds, tenantId);

        Map<UUID, List<GymUser>> usersByRoleId =
                users.stream()
                        .filter(user -> user.getRole() != null)
                        .collect(Collectors.groupingBy(
                                user -> user.getRole().getId()

                        ));

        return roles.stream()
                .map(role -> mapToResponse(
                        role,
                        usersByRoleId.getOrDefault(
                                role.getId(),
                                Collections.emptyList()
                        )
                ))
                .toList();
    }

    public List<Permission> getAllPermissions() {
        return permissionRepo.findAll();
    }


    private AuthorityResponseDto mapToResponse(Role role, List<GymUser> users) {

        return AuthorityResponseDto.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .roleDescription(role.getRoleDescription())
                .systemRole(role.getSystemRole())
                .riskLevel(calculateRisk(role))
                .userCount((long) users.size())
                .permissions(
                        role.getPermissions()
                                .stream()
                                .map(permission ->
                                        PermissionDto.builder()
                                                .id(permission.getId())
                                                .permissionCode(permission.getPermissionCode())
                                                .permissionDescription(permission.getPermissionDescription())
                                                .module(extractModule(permission.getPermissionCode()
                                                ))
                                                .build()
                                )
                                .sorted(Comparator.comparing(PermissionDto::getModule))
                                .toList()
                )

                .assignedUsers(
                        users.stream()

                                .limit(5)
                                .map(user ->
                                        RoleUserDto.builder()
                                                .id(user.getId())
                                                .fullName(user.getFullName())
                                                .email(user.getEmail())
                                                .phoneNumber(user.getPhoneNumber())
                                                .active(user.isActive())
                                                .build()
                                )
                                .toList()
                )

                .build();
    }

    private String calculateRisk(Role role) {

        Set<String> permissions =
                role.getPermissions()
                        .stream()
                        .map(Permission::getPermissionCode)
                        .collect(Collectors.toSet());

        // =========================================
        // HIGH RISK
        // =========================================

        if (
                permissions.contains("SUPER_ADMIN")
                        || permissions.contains("DELETE_MEMBER")
                        || permissions.contains("DELETE_TRAINER")
                        || permissions.contains("PAYMENT_REFUND")
                        || permissions.contains("MANAGE_BILLING")
                        || permissions.contains("ROLE_MANAGEMENT")
        ) {
            return "HIGH";
        }

        // =========================================
        // MEDIUM RISK
        // =========================================

        if (
                permissions.contains("CREATE_MEMBER")
                        || permissions.contains("UPDATE_MEMBER")
                        || permissions.contains("CREATE_PAYMENT")
                        || permissions.contains("UPDATE_TRAINER")
        ) {
            return "MEDIUM";
        }

        // =========================================
        // LOW RISK
        // =========================================

        return "LOW";
    }


    private String extractModule(String permissionCode) {

        if (permissionCode == null || permissionCode.isBlank()) {
            return "GENERAL";
        }

        String normalized = permissionCode.toUpperCase();

        if (normalized.contains("MEMBER")) {
            return "MEMBERS";
        }

        if (normalized.contains("PAYMENT")) {
            return "PAYMENTS";
        }

        if (normalized.contains("ATTENDANCE")) {
            return "ATTENDANCE";
        }

        if (normalized.contains("WORKOUT")) {
            return "WORKOUT";
        }

        if (normalized.contains("USER")) {
            return "USERS";
        }

        if (normalized.contains("PLAN")) {
            return "PLANS";
        }

        if (normalized.contains("TRAINER")) {
            return "TRAINERS";
        }

        if (normalized.contains("REPORT")) {
            return "REPORTS";
        }

        return "GENERAL";
    }
}
