package com.gym.Elite.Gym.auth.dto.authDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserToken {

    private String token;
    private UUID id;
    private String role;
    private String error;

}