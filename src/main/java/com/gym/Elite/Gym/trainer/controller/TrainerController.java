package com.gym.Elite.Gym.trainer.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.trainer.dto.TrainerRequestDTO;
import com.gym.Elite.Gym.trainer.dto.TrainerResponseDTO;
import com.gym.Elite.Gym.trainer.service.TrainerService;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    // ✅ CREATE (Admin - CRM / Website Management)
    @PostMapping("/create-trainer")
    public ResponseEntity<ResponseDto> createTrainer(@RequestBody TrainerRequestDTO request) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        ResponseDto responseDto = trainerService.createTrainer(tenantId, request);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // ✅ GET ALL TRAINERS (Admin Panel)
    @GetMapping("/get-all")
    public ResponseEntity<List<TrainerResponseDTO>> getAllTrainers() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();
        List<TrainerResponseDTO> trainerResponseDTOS=  trainerService.getAllTrainers(tenantId);
        return new ResponseEntity<>(trainerResponseDTOS, HttpStatus.OK);
    }

    // ✅ GET TRAINER BY ID
    @GetMapping("/get-trainer/{id}")
    public ResponseEntity<TrainerResponseDTO> getTrainerById(@PathVariable UUID id) {

        TrainerResponseDTO responseDTO = trainerService.getTrainerById(id);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ✅ UPDATE TRAINER
    @PutMapping("/update-trainer/{id}")
    public ResponseEntity<ResponseDto> updateTrainer(@PathVariable UUID id, @RequestBody TrainerRequestDTO request) {

        ResponseDto responseDto = trainerService.updateTrainer(id, request);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }

    // ✅ DELETE TRAINER
    @DeleteMapping("/delete-trainer/{id}")
    public ResponseEntity<ResponseDto> deleteTrainer(@PathVariable UUID id) {

        ResponseDto responseDto = trainerService.deleteTrainer(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // ✅ ACTIVATE TRAINER
    @PutMapping("/update-trainer/{id}/activate")
    public ResponseEntity<ResponseDto> activateTrainer(@PathVariable UUID id) {

        ResponseDto responseDto = trainerService.activateTrainer(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // ✅ DEACTIVATE TRAINER
    @PutMapping("/update-trainer/{id}/deactivate")
    public ResponseEntity<ResponseDto> deactivateTrainer(@PathVariable UUID id) {

        ResponseDto responseDto = trainerService.deactivateTrainer(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // ✅ TOGGLE WEBSITE VISIBILITY (🔥 main CMS control)
    @PutMapping("/update-trainer/{id}/toggle-visibility")
    public ResponseEntity<ResponseDto> toggleVisibility(@PathVariable UUID id, @RequestParam boolean visible) {

        ResponseDto responseDto = trainerService.toggleVisibility(id, visible);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // ✅ TOGGLE FEATURED
    @PutMapping("/update-trainer/{id}/toggle-featured")
    public ResponseEntity<ResponseDto> toggleFeatured(@PathVariable UUID id) {

        ResponseDto responseDto =  trainerService.toggleFeatured(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 🌍 PUBLIC API (Website)
    @GetMapping("/public")
    public ResponseEntity<List<TrainerResponseDTO>> getWebsiteTrainers() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<TrainerResponseDTO> trainerResponseDTOS = trainerService.getWebsiteTrainers(tenantId);
        return new ResponseEntity<>(trainerResponseDTOS, HttpStatus.OK);
    }
}
