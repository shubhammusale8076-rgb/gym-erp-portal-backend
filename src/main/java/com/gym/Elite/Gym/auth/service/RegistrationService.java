package com.gym.Elite.Gym.auth.service;

import com.gym.Elite.Gym.auth.dto.authDtos.RegistrationRequest;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.auth.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;

    public ResponseDto createUser(RegistrationRequest request) {

        User existing = userRepo.findByEmail(request.getEmail());

        if(null != existing){
            return  ResponseDto.builder()
                    .code(400)
                    .message("Email already exist!")
                    .build();
        }
        try{

            User user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhoneNumber(request.getPhoneNumber());

            user.setAuthority(authorityService.getUserAuthority(request.getAuthorityCode()));
            User savedUser = userRepo.save(user);

            return ResponseDto.builder()
                    .code(201)
                    .message("User created!")
                    .id(savedUser.getId())
                    .userName(savedUser.getEmail())
                    .password(savedUser.getPassword())
                    .build();


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ServerErrorException(e.getMessage(),e.getCause());
        }


    }
}
