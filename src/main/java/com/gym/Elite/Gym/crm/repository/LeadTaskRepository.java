package com.gym.Elite.Gym.crm.repository;

import com.gym.Elite.Gym.crm.entity.LeadTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadTaskRepository extends JpaRepository<LeadTask, UUID> {

    List<LeadTask> findByLeadIdAndTenantIdOrderByDueDateAsc(UUID leadId, UUID tenantId);

    Optional<LeadTask> findByIdAndTenantId(UUID id, UUID tenantId);

    long countByLeadIdAndTenantIdAndCompletedFalse(UUID leadId, UUID tenantId);
}
