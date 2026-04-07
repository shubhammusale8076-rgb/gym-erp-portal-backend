package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionPlanRepo extends JpaRepository<MemberSubscription, UUID> {
    List<MemberSubscription> findByMemberId(UUID memberId);

    List<MemberSubscription> findByStatus(String status);
}
