package com.gym.Elite.Gym.webManagement.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.webManagement.dto.testimonialDTO.TestimonialDTO;
import com.gym.Elite.Gym.webManagement.dto.testimonialDTO.TestimonialResponseDTO;
import com.gym.Elite.Gym.webManagement.service.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/testimonials")
@RequiredArgsConstructor
public class TestimonialController {

    private final TestimonialService service;

    @PostMapping("/create-testimonial")
    public ResponseEntity<ResponseDto> create(@RequestBody TestimonialDTO dto) {
        ResponseDto responseDto= service.create(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

    }

    @GetMapping("/get-all")
    public ResponseEntity<List<TestimonialResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<ResponseDto> publish(@PathVariable UUID id) {
        ResponseDto responseDto=  service.publish(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}/unpublish")
    public ResponseEntity<ResponseDto> unpublish(@PathVariable UUID id) {
        ResponseDto responseDto=  service.unpublish(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}/feature")
    public ResponseEntity<?> feature(@PathVariable UUID id) {
        service.toggleFeature(id);
        return ResponseEntity.ok("Feature updated");
    }

    @GetMapping("/public")
    public ResponseEntity<List<TestimonialResponseDTO>> getPublished() {
        return ResponseEntity.ok(service.getPublishedTestimonials());
    }
}
