package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.Role;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepo extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleCodeAndSystemRoleTrue(String roleCode);

    Role findByRoleCodeAndTenantId(String roleCode, UUID tenantId);

    @Query("""
            SELECT r.id,
                   r.roleCode,
                   r.roleDescription,
                   COUNT(u)
            FROM Role r
            LEFT JOIN GymUser u
                   ON u.role = r
                   AND u.tenantId = :tenantId
            WHERE
                   r.systemRole = true
                   OR r.tenantId = :tenantId
            GROUP BY r.id, r.roleCode, r.roleDescription
            ORDER BY r.roleCode ASC
            """)
    List<Object[]> getRolesWithUserCount(@Param("tenantId") UUID tenantId);

    @Query("""
    SELECT DISTINCT r
    FROM Role r
    LEFT JOIN FETCH r.permissions p
    WHERE (
        r.systemRole = true
        OR (
            r.systemRole = false
            AND r.tenantId = :tenantId
        )
    )
    ORDER BY r.roleCode ASC
""")
    List<Role> findAllRolesWithPermissions(
            @Param("tenantId") UUID tenantId
    );

    @Query("""
        SELECT r
        FROM Role r
        WHERE r.id = :roleId
        AND (
            r.systemRole = true
            OR (
                r.systemRole = false
                AND r.tenantId = :tenantId
            )
        )
    """)
    Optional<Role> findRoleForAssignment(
            @Param("roleId") UUID roleId,
            @Param("tenantId") UUID tenantId
    );
}
