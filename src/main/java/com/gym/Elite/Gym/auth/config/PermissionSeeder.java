package com.gym.Elite.Gym.auth.config;

import com.gym.Elite.Gym.auth.entity.Permission;
import com.gym.Elite.Gym.auth.repo.PermissionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionSeeder {

    private final PermissionRepo permissionRepo;

    public static final String CREATE_MEMBER = "CREATE_MEMBER";
    public static final String UPDATE_MEMBER = "UPDATE_MEMBER";
    public static final String DELETE_MEMBER = "DELETE_MEMBER";
    public static final String VIEW_PAYMENTS = "VIEW_PAYMENTS";
    public static final String MARK_ATTENDANCE = "MARK_ATTENDANCE";
    public static final String UPDATE_WORKOUT = "UPDATE_WORKOUT";
    public static final String MANAGE_ROLES = "MANAGE_ROLES";
    public static final String MANAGE_USERS = "MANAGE_USERS";

    private static final Map<String, String> CANONICAL_PERMISSIONS = new LinkedHashMap<>();

    static {
        CANONICAL_PERMISSIONS.put(CREATE_MEMBER, "Create gym members");
        CANONICAL_PERMISSIONS.put(UPDATE_MEMBER, "Update gym members");
        CANONICAL_PERMISSIONS.put(DELETE_MEMBER, "Delete gym members");
        CANONICAL_PERMISSIONS.put(VIEW_PAYMENTS, "View payment records");
        CANONICAL_PERMISSIONS.put(MARK_ATTENDANCE, "Mark member attendance");
        CANONICAL_PERMISSIONS.put(UPDATE_WORKOUT, "Update workout plans");
        CANONICAL_PERMISSIONS.put(MANAGE_ROLES, "Manage roles and permissions");
        CANONICAL_PERMISSIONS.put(MANAGE_USERS, "Manage staff users");
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedPermissions() {
        if (permissionRepo.count() > 0) {
            return;
        }
        CANONICAL_PERMISSIONS.forEach((code, description) ->
                permissionRepo.save(Permission.builder()
                        .permissionCode(code)
                        .permissionDescription(description)
                        .build()));
        log.info("Seeded {} canonical permissions", CANONICAL_PERMISSIONS.size());
    }
}
