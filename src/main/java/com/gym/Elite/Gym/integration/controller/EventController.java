package com.gym.Elite.Gym.integration.controller;

import com.gym.Elite.Gym.integration.dto.EventRequest;
import com.gym.Elite.Gym.integration.service.EventProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/integration/events")
@RequiredArgsConstructor
public class EventController {

    private final EventProcessorService eventProcessorService;

    @PostMapping
    public ResponseEntity<String> receiveEvent(@RequestBody EventRequest event) {
        eventProcessorService.process(event);
        return ResponseEntity.ok("Event received and processed");
    }
}
