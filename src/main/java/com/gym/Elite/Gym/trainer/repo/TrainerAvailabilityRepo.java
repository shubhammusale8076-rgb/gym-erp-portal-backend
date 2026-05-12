package com.gym.Elite.Gym.trainer.repo;

import com.gym.Elite.Gym.trainer.entity.TrainerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainerAvailabilityRepo extends JpaRepository<TrainerAvailability, UUID> {
    void deleteByTrainerId(UUID trainerId);

    List<TrainerAvailability> findByTrainerIdAndActiveTrue(UUID trainerId);
}
