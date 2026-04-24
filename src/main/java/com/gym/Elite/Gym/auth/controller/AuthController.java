package com.gym.Elite.Gym.auth.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.LoginRequest;
import com.gym.Elite.Gym.auth.dto.authDtos.RegistrationRequest;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.dto.authDtos.UserToken;
import com.gym.Elite.Gym.auth.entity.Authority;
import com.gym.Elite.Gym.auth.entity.User;
import com.gym.Elite.Gym.auth.helper.JWTTokenHelper;
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
            User user = (User) authentication.getPrincipal();

            // 5. Generate JWT token
            assert user != null;
            UUID tenantId = user.getTenantId();
            String token = jwtTokenHelper.generateToken(user.getEmail() , tenantId);

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
    public ResponseEntity<?> addRole(@RequestBody Authority authority){

        Authority authority1 = authorityService.createAuthority(authority);

        return new ResponseEntity<>(authority1, HttpStatus.CREATED);
    }

    @GetMapping("/get-roles")
    public ResponseEntity<List<Authority>> getAllAuthorities(){
        List<Authority> authorityList = authorityService.getAllAuthorities();

        return new ResponseEntity<>(authorityList, HttpStatus.OK);

    }
}
