package com.gym.Elite.Gym.attendanceEvent.controller;

import com.gym.Elite.Gym.attendanceEvent.dto.CreateSessionDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.SessionBookingDTO;
import com.gym.Elite.Gym.attendanceEvent.entity.Session;
import com.gym.Elite.Gym.attendanceEvent.service.SessionService;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/today")
    public ResponseEntity<List<Session>> getTodaySchedule() {
        return new ResponseEntity<>(sessionService.getTodaySchedule(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createSession(@RequestBody CreateSessionDTO dto) {
        return new ResponseEntity<>(sessionService.createSession(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<ResponseDto> updateSession(@PathVariable UUID sessionId, @RequestBody CreateSessionDTO dto) {
        return new ResponseEntity<>(sessionService.updateSession(sessionId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ResponseDto> deleteSession(@PathVariable UUID sessionId) {
        return new ResponseEntity<>(sessionService.deleteSession(sessionId), HttpStatus.OK);
    }

    @PostMapping("/book")
    public ResponseEntity<ResponseDto> bookSession(@RequestBody SessionBookingDTO dto) {
        return new ResponseEntity<>(sessionService.bookSession(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/book/{sessionId}/{memberId}")
    public ResponseEntity<ResponseDto> cancelBooking(@PathVariable UUID sessionId, @PathVariable UUID memberId) {
        return new ResponseEntity<>(sessionService.cancelBooking(sessionId, memberId), HttpStatus.OK);
    }
}
