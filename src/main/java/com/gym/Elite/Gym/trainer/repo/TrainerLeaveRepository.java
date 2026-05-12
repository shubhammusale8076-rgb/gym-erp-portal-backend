package com.gym.Elite.Gym.trainer.repo;

import com.gym.Elite.Gym.trainer.entity.LeaveStatus;
import com.gym.Elite.Gym.trainer.entity.TrainerLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainerLeaveRepository extends JpaRepository<TrainerLeave, UUID> {

    List<TrainerLeave> findAllByTrainerIdAndTenantId(UUID trainerId, UUID tenantId);

    @Query("SELECT l FROM TrainerLeave l WHERE l.trainerId = :trainerId AND l.tenantId = :tenantId " +
           "AND ((l.startDate <= :endDate AND l.endDate >= :startDate))")
    List<TrainerLeave> findOverlappingLeaves(@Param("trainerId") UUID trainerId, 
                                             @Param("tenantId") UUID tenantId, 
                                             @Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM TrainerLeave l WHERE l.trainerId = :trainerId AND l.tenantId = :tenantId " +
           "AND l.status = 'APPROVED' AND :date BETWEEN l.startDate AND l.endDate")
    List<TrainerLeave> findApprovedLeaveForDate(@Param("trainerId") UUID trainerId, 
                                               @Param("tenantId") UUID tenantId, 
                                               @Param("date") LocalDate date);
}
