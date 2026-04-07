package com.gym.Elite.Gym.attendanceEvent.controller;

import com.gym.Elite.Gym.attendanceEvent.dto.CreateSessionDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.SessionBookingDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.TodaySessionDTO;
import com.gym.Elite.Gym.attendanceEvent.service.SessionService;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;


    @GetMapping("/today-schedule")
    public ResponseEntity<List<TodaySessionDTO>> getTodaySchedule() {

        return ResponseEntity.ok(sessionService.getTodaySchedule());
    }

    @PostMapping("/create-session")
    public ResponseEntity<ResponseDto> createSession(@RequestBody CreateSessionDTO dto) {
        return ResponseEntity.ok(sessionService.createSession(dto));
    }

    @PutMapping("/update-session/{id}")
    public ResponseEntity<ResponseDto> updateSession(@PathVariable UUID id, @RequestBody CreateSessionDTO dto) {

        return ResponseEntity.ok(sessionService.updateSession(id, dto));
    }

    @DeleteMapping("/delete-session/{id}")
    public ResponseEntity<ResponseDto> deleteSession(@PathVariable UUID id) {

        ResponseDto responseDto =  sessionService.deleteSession(id);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{id}/book")
    public ResponseEntity<ResponseDto> bookSession(@PathVariable UUID id, @RequestBody SessionBookingDTO dto) {

        ResponseDto responseDto = sessionService.bookSession(id, dto.getMemberId());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}/cancel-booking/member/{memberId}")
    public ResponseEntity<ResponseDto> cancelBooking(
            @PathVariable UUID id,
            @PathVariable UUID memberId) {

        ResponseDto responseDto =  sessionService.cancelBooking(id, memberId);
        return ResponseEntity.ok(responseDto);

    }
}
