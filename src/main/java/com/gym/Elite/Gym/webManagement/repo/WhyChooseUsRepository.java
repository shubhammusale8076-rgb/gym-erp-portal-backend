package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.WhyChooseUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface WhyChooseUsRepository extends JpaRepository<WhyChooseUs, UUID> {
    List<WhyChooseUs> findByTenantIdOrderByDisplayOrderAsc(UUID tenantId);
    List<WhyChooseUs> findByTenantIdAndActiveTrueOrderByDisplayOrderAsc(UUID tenantId);
}
