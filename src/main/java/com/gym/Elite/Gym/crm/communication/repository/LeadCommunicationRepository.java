package com.gym.Elite.Gym.crm.communication.repository;

import com.gym.Elite.Gym.crm.communication.entity.LeadCommunication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeadCommunicationRepository extends JpaRepository<LeadCommunication, Long> {
    List<LeadCommunication> findByLeadIdAndTenantIdOrderByCreatedAtDesc(UUID leadId, UUID tenantId);
}
