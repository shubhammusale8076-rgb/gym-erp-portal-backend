package com.gym.Elite.Gym.auth.dto.authDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserDto {

    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private Boolean active;
}