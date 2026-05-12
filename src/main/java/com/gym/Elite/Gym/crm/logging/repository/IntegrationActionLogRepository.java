package com.gym.Elite.Gym.crm.logging.repository;

import com.gym.Elite.Gym.crm.logging.entity.IntegrationActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationActionLogRepository extends JpaRepository<IntegrationActionLog, Long> {
    List<IntegrationActionLog> findByCorrelationIdAndTenantIdOrderByCreatedAtDesc(String correlationId, UUID tenantId);
    List<IntegrationActionLog> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}
