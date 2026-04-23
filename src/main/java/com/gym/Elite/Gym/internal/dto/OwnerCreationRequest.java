package com.gym.Elite.Gym.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerCreationRequest {
    private UUID tenantId;
    private String email;
    private String name;
}
