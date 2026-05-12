package com.gym.Elite.Gym.trainer.repo;

import com.gym.Elite.Gym.trainer.entity.TrainerMemberAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainerMemberAssignmentRepo extends JpaRepository<TrainerMemberAssignment, UUID> {
    long countByTrainerIdAndTenantIdAndActiveTrue(UUID trainerId, UUID tenantId);

    List<TrainerMemberAssignment> findByTrainerIdAndActiveTrue(UUID trainerId);

    Optional<TrainerMemberAssignment> findByMemberIdAndTenantIdAndActiveTrue(UUID memberId, UUID trainerId);

    Optional<TrainerMemberAssignment> findByTrainerIdAndMemberIdAndTenantIdAndActiveTrue(UUID trainerId, UUID memberId, UUID tenantId);}
