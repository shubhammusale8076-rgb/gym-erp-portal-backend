package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.MemberSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionPlanRepo extends JpaRepository<MemberSubscription, UUID> {


    @Query("""
        SELECT p.name, COUNT(s.id)
        FROM MemberSubscription s
        JOIN s.plan p
        WHERE s.active = true 
          AND s.status = com.gym.Elite.Gym.auth.entity.SubscriptionStatus.ACTIVE
          AND p.tenantId = :tenantId
        GROUP BY p.name
    """)
    List<Object[]> getPlanWiseCounts(UUID tenantId);

    @Query("""
        SELECT COUNT(s.id)
        FROM MemberSubscription s
        WHERE s.active = true 
          AND s.status = com.gym.Elite.Gym.auth.entity.SubscriptionStatus.ACTIVE
          AND s.tenantId = :tenantId
    """)
    long getTotalActiveSubscriptions(UUID tenantId);

    Optional<MemberSubscription> findTopByMemberIdAndTenantIdOrderByCreatedOnDesc(UUID memberId, UUID tenantId);

    @Query("""
SELECT ms FROM MemberSubscription ms
WHERE ms.tenantId = :tenantId
AND ms.createdOn = (
    SELECT MAX(ms2.createdOn)
    FROM MemberSubscription ms2
    WHERE ms2.member.id = ms.member.id
)
""")
    List<MemberSubscription> findLatestSubscriptions(UUID tenantId);

    @Query("""
        SELECT s
        FROM MemberSubscription s
        WHERE s.tenantId = :tenantId
        AND s.member.id = :memberId
        AND s.status = 'ACTIVE'
    """)
    Optional<MemberSubscription> findActiveSubscription(
            UUID tenantId,
            UUID memberId
    );

    @Query("""
        SELECT s
        FROM MemberSubscription s
        WHERE s.tenantId = :tenantId
        AND s.member.id = :memberId
        ORDER BY s.createdOn DESC
    """)
    List<MemberSubscription> findSubscriptionHistory(
            @Param("tenantId") UUID tenantId,
            @Param("memberId") UUID memberId
    );

    Optional<MemberSubscription> findByIdAndTenantId(UUID subscriptionId, UUID tenantId);
}
