package com.gym.Elite.Gym.webManagement.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.utility.SecurityUtils;
import com.gym.Elite.Gym.webManagement.dto.galleryDto.GalleryAssetRequestDTO;
import com.gym.Elite.Gym.webManagement.dto.galleryDto.GalleryAssetResponseDTO;
import com.gym.Elite.Gym.webManagement.service.CloudinaryService;
import com.gym.Elite.Gym.webManagement.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;
    private final CloudinaryService cloudinaryService;

    @PostMapping("/create-gallery")
    public ResponseEntity<ResponseDto> create(@RequestBody GalleryAssetRequestDTO dto) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        ResponseDto responseDto =  galleryService.create(tenantId, dto);
        return  new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<GalleryAssetResponseDTO>> getAll() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<GalleryAssetResponseDTO> responseDTOS= galleryService.getAll(tenantId);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @GetMapping("/public")
    public ResponseEntity<List<GalleryAssetResponseDTO>> getVisible() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<GalleryAssetResponseDTO> responseDTOS=  galleryService.getVisibleAssets(tenantId);

        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @PutMapping("/update-gallery/{id}")
    public ResponseEntity<ResponseDto> update(@PathVariable UUID id, @RequestBody GalleryAssetRequestDTO dto) {

        ResponseDto responseDTOS = galleryService.update(id, dto);

        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete-gallery/{id}")
    public ResponseEntity<ResponseDto> delete(@PathVariable UUID id) {
        ResponseDto responseDTOS = galleryService.delete(id);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @PutMapping("/update-gallery/{id}/toggle-visibility")
    public ResponseEntity<ResponseDto> toggleVisibility(@PathVariable UUID id) {
        ResponseDto responseDTOS = galleryService.toggleVisibility(id);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @GetMapping("/upload-config")
    public ResponseEntity<?> getUploadConfig() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Map<String, Object> config = cloudinaryService.generateUploadSignature(tenantId);

        return ResponseEntity.ok(config);
    }
}
