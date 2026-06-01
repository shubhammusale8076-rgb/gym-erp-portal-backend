package com.gym.Elite.Gym.integration.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.integration.service.WhatsappService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/integrations/whatsapp")
@RequiredArgsConstructor
public class WhatsappController {

    private final WhatsappService whatsappService;

    @PostMapping("/send-welcome-msg/{memberId}")
    public ResponseEntity<ResponseDto> sendWelcomeMsg(@PathVariable UUID memberId){

        ResponseDto responseDto = whatsappService.sendWelcomeMsg(memberId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
