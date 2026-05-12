package com.gym.Elite.Gym.trainer.repo;

import com.gym.Elite.Gym.trainer.entity.TrainerAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainerAttendanceRepository extends JpaRepository<TrainerAttendance, UUID> {

    Optional<TrainerAttendance> findByTrainerIdAndDateAndTenantId(UUID trainerId, LocalDate date, UUID tenantId);

    List<TrainerAttendance> findAllByTrainerIdAndDateBetweenAndTenantId(UUID trainerId, LocalDate startDate, LocalDate endDate, UUID tenantId);

    List<TrainerAttendance> findAllByTenantIdAndCheckInTimeIsNotNullAndCheckOutTimeIsNull(UUID tenantId);
}
