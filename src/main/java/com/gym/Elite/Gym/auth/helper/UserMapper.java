package com.gym.Elite.Gym.auth.helper;


import com.gym.Elite.Gym.auth.dto.userDtos.UserDetailsDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserListDto;
import com.gym.Elite.Gym.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDetailsDto toDto(User user) {

        return UserDetailsDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .authority(
                        user.getAuthorities()
                                .stream()
                                .findFirst()
                                .map(GrantedAuthority::getAuthority)
                                .orElse(null)
                )
                .build();
    }

    public UserListDto toListDto(User user) {

        return UserListDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .authority(
                        user.getAuthorities()
                                .stream()
                                .findFirst()
                                .map(GrantedAuthority::getAuthority)
                                .orElse(null)
                )
                .creationDate(user.getCreatedOn())
                .build();
    }
}
