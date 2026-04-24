package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactInfoRepo extends JpaRepository<ContactInfo, UUID> {

    Optional<ContactInfo> findByTenantIdAndStatus(UUID tenantId, String status);

}
