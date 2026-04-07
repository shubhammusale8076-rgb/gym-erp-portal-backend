package com.gym.Elite.Gym.webManagement.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.utility.SecurityUtils;
import com.gym.Elite.Gym.webManagement.dto.heroDto.HeroDTO;
import com.gym.Elite.Gym.webManagement.service.HeroSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hero-section")
@RequiredArgsConstructor
public class HeroController {

    private final HeroSectionService heroSectionService;

    @GetMapping("/get-section")
    public ResponseEntity<HeroDTO> get() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        HeroDTO dto =  heroSectionService.get(tenantId);
         return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @PostMapping("/save-draft")
    public ResponseEntity<ResponseDto> saveDraft(@RequestBody HeroDTO dto) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        ResponseDto responseDto = heroSectionService.saveDraft(tenantId, dto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/publish")
    public ResponseEntity<ResponseDto> publish(@RequestBody HeroDTO dto) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        ResponseDto responseDto = heroSectionService.publish(tenantId, dto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}
