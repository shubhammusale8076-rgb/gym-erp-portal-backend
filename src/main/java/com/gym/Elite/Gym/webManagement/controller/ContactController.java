package com.gym.Elite.Gym.webManagement.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.webManagement.dto.contactUsDto.ContactPageDTO;
import com.gym.Elite.Gym.webManagement.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;


    @GetMapping("/get-contact")
    public ResponseEntity<ContactPageDTO> getPublished() {
        return ResponseEntity.ok(contactService.getPublished());
    }

    // ✅ Admin (CMS)
    @GetMapping("/get-draft")
    public ResponseEntity<ContactPageDTO> getDraft() {
        return ResponseEntity.ok(contactService.getDraft());
    }

    // 🟡 Save Draft
    @PostMapping("/save-draft")
    public ResponseEntity<ResponseDto> saveDraft(@RequestBody ContactPageDTO dto) {
        ResponseDto responseDto =  contactService.saveDraft(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/publish")
    public ResponseEntity<ResponseDto> publish(@RequestBody ContactPageDTO dto) {
        ResponseDto responseDto =  contactService.publish(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
