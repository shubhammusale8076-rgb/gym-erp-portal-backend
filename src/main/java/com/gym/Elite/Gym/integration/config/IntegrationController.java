package com.gym.Elite.Gym.integration.config;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.integration.dto.IntegrationRequest;
import com.gym.Elite.Gym.integration.entity.Integration;
import com.gym.Elite.Gym.integration.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationService integrationService;

    @PostMapping("/connect")
    public ResponseEntity<ResponseDto> connect(@RequestBody IntegrationRequest request) {

        ResponseDto responseDto = integrationService.connect(request.getTenantId(), request.getProvider(), request.getConfig());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
