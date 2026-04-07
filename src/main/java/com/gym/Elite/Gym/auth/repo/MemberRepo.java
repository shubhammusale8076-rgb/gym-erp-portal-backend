package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepo extends JpaRepository<Member, UUID> {

    List<Member> findByTenantId(UUID tenantId);

    boolean existsByEmail(String email);
}
