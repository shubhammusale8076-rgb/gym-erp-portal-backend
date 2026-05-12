package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.FaqItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface FaqItemRepository extends JpaRepository<FaqItem, UUID> {
    List<FaqItem> findByTenantIdOrderByDisplayOrderAsc(UUID tenantId);
    List<FaqItem> findByTenantIdAndActiveTrueOrderByDisplayOrderAsc(UUID tenantId);
}
