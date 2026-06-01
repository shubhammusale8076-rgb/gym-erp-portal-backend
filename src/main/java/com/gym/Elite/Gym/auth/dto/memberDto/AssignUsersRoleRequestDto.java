package com.gym.Elite.Gym.auth.dto.memberDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignUsersRoleRequestDto {

    @NotNull
    private UUID roleId;

    @NotEmpty
    private List<UUID> userIds;
}
