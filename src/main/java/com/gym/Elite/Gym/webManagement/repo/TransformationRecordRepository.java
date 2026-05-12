package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.TransformationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransformationRecordRepository extends JpaRepository<TransformationRecord, UUID> {
    List<TransformationRecord> findByTenantIdOrderByDisplayOrderAsc(UUID tenantId);
    List<TransformationRecord> findByTenantIdAndActiveTrueOrderByDisplayOrderAsc(UUID tenantId);
}
