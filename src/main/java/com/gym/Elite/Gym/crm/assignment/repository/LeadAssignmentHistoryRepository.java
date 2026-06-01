package com.gym.Elite.Gym.crm.assignment.repository;

import com.gym.Elite.Gym.crm.assignment.entity.LeadAssignmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeadAssignmentHistoryRepository extends JpaRepository<LeadAssignmentHistory, Long> {
    List<LeadAssignmentHistory>
    findByLeadIdAndTenantIdOrderByChangedAtDesc(
            UUID leadId,
            UUID tenantId
    );}
