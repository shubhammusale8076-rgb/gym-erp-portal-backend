package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.GymUser;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GymUserRepo extends JpaRepository<GymUser, UUID> {


    @Query("""
    SELECT u
    FROM GymUser u
    WHERE u.tenantId = :tenantId
    AND (
        u.role IS NULL OR
        UPPER(u.role.roleCode) NOT IN ('MEMBER', 'TRAINER')
    )
""")
    List<GymUser> findNonMemberAndTrainerUsers(UUID tenantId);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);

    List<GymUser> findByTenantId(UUID tenantId);

    List<GymUser> findByTenantIdAndEnabled(UUID tenantId, boolean enabled);


    List<GymUser> findAllByEmail(String email);

    @Query("""
            SELECT DISTINCT u FROM GymUser u
            LEFT JOIN FETCH u.role r
            LEFT JOIN FETCH r.permissions
            WHERE u.email = :email
            """)
    Optional<GymUser> findByEmailWithRoleAndPermissions(@Param("email") String email);

    @Query("""
            SELECT DISTINCT u FROM GymUser u
            LEFT JOIN FETCH u.role r
            LEFT JOIN FETCH r.permissions
            WHERE u.email = :email AND u.tenantId = :tenantId
            """)
    Optional<GymUser> findByEmailAndTenantIdWithRoleAndPermissions(
            @Param("email") String email,
            @Param("tenantId") UUID tenantId);


    @Query("""
    SELECT u
    FROM GymUser u
    WHERE u.role.id IN :roleIds
      AND u.tenantId = :tenantId
""")
    List<GymUser> findUsersByRoleIdsAndTenant(
            @Param("roleIds") List<UUID> roleIds,
            @Param("tenantId") UUID tenantId
    );


    @Query("""
        SELECT u
        FROM GymUser u
        LEFT JOIN FETCH u.role r
        WHERE (
            LOWER(u.fullName)
                LIKE LOWER(CONCAT('%', :query, '%'))

            OR

            LOWER(u.email)
                LIKE LOWER(CONCAT('%', :query, '%'))
        )

        AND u.tenantId = :tenantId

        ORDER BY u.fullName ASC
    """)
    List<GymUser> searchUsers(
            @Param("query") String query,
            @Param("tenantId") UUID tenantId
    );
    @Query("""
        SELECT u
        FROM GymUser u
        WHERE u.id IN :userIds
          AND u.tenantId = :tenantId
    """)
    List<GymUser> findUsersForRoleAssignment(
            @Param("userIds") List<UUID> userIds,
            @Param("tenantId") UUID tenantId
    );
}
