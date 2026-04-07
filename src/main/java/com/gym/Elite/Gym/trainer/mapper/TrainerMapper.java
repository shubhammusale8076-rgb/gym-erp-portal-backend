package com.gym.Elite.Gym.trainer.mapper;

import com.gym.Elite.Gym.trainer.dto.TrainerResponseDTO;
import com.gym.Elite.Gym.trainer.entity.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public TrainerResponseDTO mapToTrainerDTO(Trainer trainer) {
        return TrainerResponseDTO.builder()
                .id(trainer.getId())
                .fullName(trainer.getFullName())
                .email(trainer.getEmail())
                .phoneNumber(trainer.getPhoneNumber())
                .experienceInYears(trainer.getExperienceInYears())
                .bio(trainer.getBio())
                .profileImageUrl(trainer.getProfileImageUrl())
                .available(trainer.getAvailable())
                .active(trainer.getActive())
                .visibleOnWebsite(trainer.getVisibleOnWebsite())
                .build();
    }}
