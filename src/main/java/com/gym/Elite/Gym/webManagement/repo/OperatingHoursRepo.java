package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.OperatingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OperatingHoursRepo extends JpaRepository<OperatingHours, UUID> {

    List<OperatingHours> findByTenantIdAndStatus(UUID tenantId, String status);

    void deleteByTenantIdAndStatus(UUID tenantId, String status);
}
