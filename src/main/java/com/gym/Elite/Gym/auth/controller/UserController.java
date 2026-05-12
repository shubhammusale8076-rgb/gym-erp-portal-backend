package com.gym.Elite.Gym.auth.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UpdateUserDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserDetailsDto;
import com.gym.Elite.Gym.auth.dto.userDtos.UserListDto;
import com.gym.Elite.Gym.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update-user/{id}")
    public ResponseEntity<ResponseDto> updateUser(@PathVariable UUID id, @RequestBody UpdateUserDto dto) {
        ResponseDto responseDto = userService.updateUser(id, dto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }

    // 🔹 Delete User
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable UUID id) {

        ResponseDto responseDto = userService.deleteUser(id);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }



}
