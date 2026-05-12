package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorityRepo extends JpaRepository<Authority, Integer> {

    Authority findByRoleCode(String authorityCode);
    Authority findByRoleCodeAndTenantId(String roleCode, UUID tenantId);

    @Query("""
    SELECT a.id, a.roleCode, a.roleDescription, COUNT(u)
    FROM Authority a
    LEFT JOIN User u 
        ON u.authority = a AND u.tenantId = :tenantId
    GROUP BY a.id, a.roleCode, a.roleDescription
""")
    List<Object[]> getAuthoritiesWithUserCount(UUID tenantId);
}
