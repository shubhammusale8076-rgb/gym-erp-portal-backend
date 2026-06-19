package com.gym.Elite.Gym.trainer.repo;

import com.gym.Elite.Gym.trainer.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainerRepo extends JpaRepository<Trainer, UUID> {

    List<Trainer> findByTenantId(UUID tenantId);

    List<Trainer> findByTenantIdAndVisibleOnWebsiteTrueAndActiveTrue(UUID tenantId);

    Optional<Trainer> findByIdAndTenantId(UUID trainerId, UUID tenantId);

    @Query("""
       SELECT t
       FROM Trainer t
       WHERE t.tenantId = :tenantId
       AND (
           LOWER(t.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(t.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))
       )
       AND t.active = true
       """)
    List<Trainer> searchAttendanceTrainers(UUID tenantId, String query);
}
