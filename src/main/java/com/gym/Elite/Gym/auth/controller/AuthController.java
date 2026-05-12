package com.gym.Elite.Gym.auth.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.*;
import com.gym.Elite.Gym.auth.entity.Authority;
import com.gym.Elite.Gym.auth.entity.CustomUserDetails;
import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.auth.helper.JWTTokenHelper;
import com.gym.Elite.Gym.auth.repo.UserRepo;
import com.gym.Elite.Gym.auth.service.AuthorityService;
import com.gym.Elite.Gym.auth.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthorityService authorityService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenHelper jwtTokenHelper;
    private final UserRepo userRepo;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegistrationRequest request){

        ResponseDto registrationResponse = registrationService.createUser(request);

        return new ResponseEntity<>(registrationResponse,
                registrationResponse.getCode() == 201 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/login")
    public ResponseEntity<UserToken> login(@RequestBody LoginRequest loginRequest) {

        try{

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    );

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. Get user details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            User user = userRepo.findByEmail(userDetails.getUsername());

            user.setLastLogin(LocalDateTime.now());
            userRepo.save(user);

            String token = jwtTokenHelper.generateToken(
                    userDetails.getUsername(),
                    userDetails.getTenantId(),
                    userDetails.getTokenVersion()
            );

            // 6. Return response
            return ResponseEntity.ok(
                    UserToken.builder()
                            .token(token)
                            .id(user.getId())
                            .role(user.getAuthority().getRoleCode())
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(UserToken.builder()
                            .error("Invalid username or password")
                            .build());
        }

    }

    @PostMapping("/addrole")
    public ResponseEntity<?> addRole(@RequestBody AuthorityRequestDto authority){

        ResponseDto authority1 = authorityService.createAuthority(authority);

        return new ResponseEntity<>(authority1, HttpStatus.CREATED);
    }

    @GetMapping("/get-roles")
    public ResponseEntity<List<AuthorityResponseDto>> getAllAuthorities(){
        List<AuthorityResponseDto> authorityList = authorityService.getAllAuthorities();

        return new ResponseEntity<>(authorityList, HttpStatus.OK);

    }
}
