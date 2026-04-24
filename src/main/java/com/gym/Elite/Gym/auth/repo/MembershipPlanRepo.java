package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface MembershipPlanRepo extends JpaRepository<MembershipPlan, UUID> {
    Collection<MembershipPlan> findByTenantId(String tenantId);

}
