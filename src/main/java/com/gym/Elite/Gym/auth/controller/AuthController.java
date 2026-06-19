package com.gym.Elite.Gym.auth.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.*;
import com.gym.Elite.Gym.auth.entity.CustomUserDetails;
import com.gym.Elite.Gym.auth.entity.GymUser;
import com.gym.Elite.Gym.auth.helper.JWTTokenHelper;
import com.gym.Elite.Gym.auth.repo.GymUserRepo;
import com.gym.Elite.Gym.auth.service.AuthorityService;
import com.gym.Elite.Gym.auth.service.RegistrationService;
import com.gym.Elite.Gym.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class  AuthController {

    private final RegistrationService registrationService;
    private final AuthorityService authorityService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenHelper jwtTokenHelper;
    private final GymUserRepo gymUserRepo;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegistrationRequest request) {
        ResponseDto registrationResponse = registrationService.createUser(request);
        return new ResponseEntity<>(registrationResponse,
                registrationResponse.getCode() == 201 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    public ResponseEntity<UserToken> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("LOGIN REQUEST RECEIVED");
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    );

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            if (loginRequest.getTenantId() != null
                    && !loginRequest.getTenantId().equals(userDetails.getTenantId())) {
                return unauthorized();
            }

            GymUser gymUser = gymUserRepo.findById(userDetails.getId()).orElseThrow();

            gymUser.setLastLogin(LocalDateTime.now());
            gymUserRepo.save(gymUser);

            List<String> permissions = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> !auth.startsWith("ROLE_"))
                    .collect(Collectors.toList());

            String token = jwtTokenHelper.generateTokenFromAuthorities(
                    userDetails.getUsername(),
                    userDetails.getTenantId(),
                    userDetails.getTokenVersion(),
                    userDetails.getId(),
                    userDetails.getRoleCode(),
                    userDetails.getAuthorities()
            );

            return ResponseEntity.ok(
                    UserToken.builder()
                            .token(token)
                            .id(gymUser.getId())
                            .role(userDetails.getRoleCode())
                            .permissions(permissions)
                            .build()
            );

        } catch (Exception e) {
            return unauthorized();
        }
    }

    private ResponseEntity<UserToken> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(UserToken.builder()
                        .error("Invalid username or password")
                        .build());
    }

    @GetMapping("/get-roles")
    public ResponseEntity<List<AuthorityResponseDto>> getAllAuthorities() {
        List<AuthorityResponseDto> authorityList = authorityService.getAllAuthorities();
        return new ResponseEntity<>(authorityList, HttpStatus.OK);
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponseDto>> getAllPermissions() {
        List<PermissionResponseDto> permissions = roleService.getAllPermissions().stream()
                .map(p -> PermissionResponseDto.builder()
                        .id(p.getId())
                        .permissionCode(p.getPermissionCode())
                        .permissionDescription(p.getPermissionDescription())
                        .build())
                .toList();
        return ResponseEntity.ok(permissions);
    }

    @PostMapping ("/reset-password/{memberId}")
    public ResponseEntity<ResponseDto> resetPassword(@PathVariable UUID memberId){

        ResponseDto responseDto = registrationService.adminResetMemberPassword(memberId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
