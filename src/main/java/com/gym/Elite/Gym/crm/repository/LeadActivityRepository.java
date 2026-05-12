package com.gym.Elite.Gym.crm.repository;

import com.gym.Elite.Gym.crm.entity.LeadActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeadActivityRepository extends JpaRepository<LeadActivity, UUID> {

    List<LeadActivity> findByLeadIdAndTenantIdOrderByCreatedAtDesc(UUID leadId, UUID tenantId);
}
