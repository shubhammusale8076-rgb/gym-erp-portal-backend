package com.gym.Elite.Gym.crm.repository;

import com.gym.Elite.Gym.crm.entity.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, UUID> {

    Optional<FollowUp> findByIdAndTenantId(UUID id, UUID tenantId);

    List<FollowUp> findByLeadIdAndTenantIdOrderByFollowUpAtAsc(UUID leadId, UUID tenantId);

    // ─── Today's follow-ups ──────────────────────────────────────────────────

    @Query("SELECT f FROM FollowUp f WHERE f.tenantId = :tenantId " +
           "AND f.completed = false " +
           "AND f.followUpAt >= :startOfDay AND f.followUpAt < :endOfDay " +
           "ORDER BY f.followUpAt ASC")
    List<FollowUp> findTodayFollowUps(@Param("tenantId") UUID tenantId,
                                       @Param("startOfDay") LocalDateTime startOfDay,
                                       @Param("endOfDay") LocalDateTime endOfDay);

    // ─── Overdue ─────────────────────────────────────────────────────────────

    @Query("SELECT f FROM FollowUp f WHERE f.tenantId = :tenantId " +
           "AND f.completed = false AND f.overdue = true " +
           "ORDER BY f.followUpAt ASC")
    List<FollowUp> findOverdueFollowUps(@Param("tenantId") UUID tenantId);

    // ─── Scheduler: mark overdue ─────────────────────────────────────────────

    @Query("SELECT f FROM FollowUp f WHERE f.completed = false " +
           "AND f.overdue = false AND f.followUpAt < :now")
    List<FollowUp> findAllPendingOverdueGlobal(@Param("now") LocalDateTime now);

    // ─── Stats ───────────────────────────────────────────────────────────────

    long countByTenantIdAndCompletedFalseAndOverdueTrue(UUID tenantId);

    long countByLeadIdAndTenantId(UUID leadId, UUID tenantId);

    long countByLeadIdAndTenantIdAndCompletedTrue(UUID leadId, UUID tenantId);
}
