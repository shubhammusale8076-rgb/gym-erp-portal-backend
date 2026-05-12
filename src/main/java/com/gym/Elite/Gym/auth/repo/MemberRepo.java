package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepo extends JpaRepository<Member, UUID> {

    List<Member> findByTenantId(UUID tenantId);

    Optional<Member> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);

    @Query("""
    SELECT DISTINCT m FROM Member m
    LEFT JOIN FETCH m.subscriptions s
    LEFT JOIN FETCH s.plan
    WHERE m.tenantId = :tenantId
""")
    List<Member> findAllWithSubscriptions(UUID tenantId);
}
