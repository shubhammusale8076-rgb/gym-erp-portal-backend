package com.gym.Elite.Gym.auth.dto.userDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListDto {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private boolean enabled;
    private String authority;
    private Date creationDate;
    private Date lastLoginDate;
}
