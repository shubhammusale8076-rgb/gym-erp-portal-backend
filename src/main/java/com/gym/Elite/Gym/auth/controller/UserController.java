package com.gym.Elite.Gym.auth.controller;

import com.gym.Elite.Gym.auth.dto.userDtos.UserDetailsDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserListDto;
import com.gym.Elite.Gym.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDetailsDto> getUserProfile(@PathVariable UUID id){

        UserDetailsDto userDetailsDto = userService.getUserProfile(id);

        return new ResponseEntity<>(userDetailsDto, HttpStatus.OK);
    }

    @GetMapping("/get-user-list")
    public ResponseEntity<List<UserListDto>> getAllUsers() {
        List<UserListDto> userDetailsDtoList =  userService.getAllUsers();

        return new ResponseEntity<>(userDetailsDtoList, HttpStatus.OK);
    }



}
