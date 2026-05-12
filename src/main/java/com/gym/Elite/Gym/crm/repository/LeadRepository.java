package com.gym.Elite.Gym.crm.repository;

import com.gym.Elite.Gym.crm.entity.Lead;
import com.gym.Elite.Gym.crm.enums.LeadPriority;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID>, JpaSpecificationExecutor<Lead> {

    // ─── Existence checks ────────────────────────────────────────────────────

    boolean existsByPhoneAndTenantIdAndDeletedFalse(String phone, UUID tenantId);

    boolean existsByPhoneAndTenantIdAndDeletedFalseAndIdNot(String phone, UUID tenantId, UUID id);

    // ─── Tenant-scoped single fetch ──────────────────────────────────────────

    Optional<Lead> findByIdAndTenantIdAndDeletedFalse(UUID id, UUID tenantId);

    // ─── Dashboard counts ────────────────────────────────────────────────────

    long countByTenantIdAndDeletedFalse(UUID tenantId);

    long countByTenantIdAndDeletedFalseAndCreatedAtAfter(UUID tenantId, LocalDateTime after);

    long countByTenantIdAndDeletedFalseAndFollowUpOverdueTrue(UUID tenantId);

    long countByTenantIdAndDeletedFalseAndStage(UUID tenantId, LeadStage stage);

    long countByTenantIdAndDeletedFalseAndPriority(UUID tenantId, LeadPriority priority);

    long countByTenantIdAndDeletedFalseAndConvertedTrue(UUID tenantId);

    // ─── Revenue pipeline ────────────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(l.expectedRevenue), 0) FROM Lead l " +
           "WHERE l.tenantId = :tenantId AND l.deleted = false " +
           "AND l.stage NOT IN ('CONVERTED', 'LOST')")
    Double sumRevenuePipelineByTenant(@Param("tenantId") UUID tenantId);

    // ─── Source performance ───────────────────────────────────────────────────

    @Query("SELECT l.source, COUNT(l), " +
           "SUM(CASE WHEN l.converted = true THEN 1 ELSE 0 END), " +
           "COALESCE(SUM(l.expectedRevenue), 0) " +
           "FROM Lead l WHERE l.tenantId = :tenantId AND l.deleted = false " +
           "GROUP BY l.source")
    List<Object[]> findSourcePerformanceByTenant(@Param("tenantId") UUID tenantId);

    // ─── Overdue scheduler ───────────────────────────────────────────────────

    @Modifying
    @Query("UPDATE Lead l SET l.followUpOverdue = true " +
           "WHERE l.tenantId = :tenantId AND l.deleted = false " +
           "AND l.nextFollowUpAt < :now AND l.converted = false " +
           "AND (l.stage != 'CONVERTED' AND l.stage != 'LOST')")
    int markOverdueLeads(@Param("tenantId") UUID tenantId, @Param("now") LocalDateTime now);

    // ─── Conversion analytics ────────────────────────────────────────────────

    @Query("SELECT AVG(l.leadScore) FROM Lead l " +
           "WHERE l.tenantId = :tenantId AND l.deleted = false")
    Double findAvgLeadScore(@Param("tenantId") UUID tenantId);
}
