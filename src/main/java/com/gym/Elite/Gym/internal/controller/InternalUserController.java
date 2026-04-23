package com.gym.Elite.Gym.internal.controller;

import com.gym.Elite.Gym.auth.service.UserService;
import com.gym.Elite.Gym.internal.dto.OwnerCreationRequest;
import com.gym.Elite.Gym.internal.dto.OwnerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/owners")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @Value("${internal.api.secret}")
    private String internalSecret;

    @PostMapping
    public ResponseEntity<?> createOwner(
            @RequestHeader(value = "X-Internal-Secret", required = false) String secret,
            @RequestBody OwnerCreationRequest request) {

        if (secret == null || !secret.equals(internalSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or missing internal secret");
        }

        try {
            OwnerResponse response = userService.createOwner(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
