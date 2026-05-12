package com.gym.Elite.Gym.auth.dto.authDtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityRequestDto {

    private String roleCode;
    private String roleDescription;
}