package com.gym.Elite.Gym.auth.helper;

import com.gym.Elite.Gym.auth.dto.userDtos.UserDetailsDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserListDto;
import com.gym.Elite.Gym.auth.entity.GymUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDetailsDto toDto(GymUser user) {
        return UserDetailsDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .authority(
                        user.getRole() != null
                                ? user.getRole().getRoleCode()
                                : null
                )
                .build();
    }

    public UserListDto toListDto(GymUser user) {
        return UserListDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .authority(
                        user.getRole() != null
                                ? "ROLE_" + user.getRole().getRoleCode()
                                : null
                )
                .creationDate(user.getCreatedOn())
                .lastLoginDate(user.getLastLogin())
                .build();
    }
}
