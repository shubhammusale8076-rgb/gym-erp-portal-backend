package com.gym.Elite.Gym.auth.dto.authDtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityResponseDto {

    private UUID id;

    private String roleCode;

    private String roleDescription;

    private Boolean systemRole;

    private Boolean defaultRole;

    private Long userCount;

    private String riskLevel;

    private List<PermissionDto> permissions;

    private List<RoleUserDto> assignedUsers;

}

