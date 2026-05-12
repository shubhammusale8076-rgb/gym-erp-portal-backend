package com.gym.Elite.Gym.common.controller;


import com.gym.Elite.Gym.common.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    // 🔥 Upload API
    @PostMapping("/profile")
    public ResponseEntity<Map<String, String>> uploadProfile(
            @RequestParam("file") MultipartFile file
    ) {
        Map<String, String> result = cloudinaryService.uploadImage(file);
        return ResponseEntity.ok(result);
    }

    // 🔥 Delete API
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(
            @RequestParam("publicId") String publicId
    ) {
        cloudinaryService.deleteImage(publicId);
        return ResponseEntity.ok().build();
    }
}
