package com.gym.Elite.Gym.integration.controller;


import com.gym.Elite.Gym.integration.dto.google.GoogleSheetExportResponse;
import com.gym.Elite.Gym.integration.service.GoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integrations/google")
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleService googleService;


    @PostMapping("/export-members")
    public ResponseEntity<GoogleSheetExportResponse> exportMembersToGoogleSheets() {

        return ResponseEntity.ok(googleService.exportMembersToGoogleSheets());
    }
}
