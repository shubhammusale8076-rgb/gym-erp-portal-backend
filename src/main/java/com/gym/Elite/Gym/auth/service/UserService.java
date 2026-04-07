package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.userDtos.UserDetailsDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserListDto;
import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.auth.helper.UserMapper;
import com.gym.Elite.Gym.auth.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;


    public UserDetailsDto getUserProfile(UUID id) {

        User user = userRepo.findById(id).orElse(null);
        assert user != null;
        return  userMapper.toDto(user);
    }

    public List<UserListDto> getAllUsers() {
        List<User> userList = userRepo.findAll();

        return userList.stream().map(userMapper::toListDto).toList();
    }
}
