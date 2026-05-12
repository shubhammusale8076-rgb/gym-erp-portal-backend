package com.gym.Elite.Gym.crm.repository;

import com.gym.Elite.Gym.crm.entity.LeadNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeadNoteRepository extends JpaRepository<LeadNote, UUID> {

    List<LeadNote> findByLeadIdAndTenantIdOrderByCreatedAtDesc(UUID leadId, UUID tenantId);
}
