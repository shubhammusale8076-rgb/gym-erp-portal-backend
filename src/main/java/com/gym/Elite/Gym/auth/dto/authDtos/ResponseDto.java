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
public class ResponseDto {

    private int code;
    private String message;
    private UUID id;
    private String userName;
    private String password;

}
