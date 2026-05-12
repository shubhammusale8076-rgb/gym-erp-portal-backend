package com.gym.Elite.Gym.trainer.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignMembersRequest {

    private UUID trainerId;
    private List<UUID> memberIds;
}
