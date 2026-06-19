package com.gym.Elite.Gym.attendanceEvent.controller;

import com.gym.Elite.Gym.attendanceEvent.dto.*;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Unified Attendance API.
 * Supports Members, Trainers, and Staff.
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * Manual check-in for any actor type (Member, Trainer, Staff).
     */
    @PostMapping("/manual")
    public ResponseEntity<AttendanceEventResponseDto> manualCheckIn(@Valid @RequestBody ManualAttendanceRequest request) {

        AttendanceEventDto eventDto = AttendanceEventDto.builder()
                .actorId(request.getActorId())
                .actorType(request.getActorType())
                .source(AttendanceSource.MANUAL)
                .timestamp(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        AttendanceEventResponseDto response = attendanceService.recordAttendanceEvent(eventDto);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<List<AttendanceActorSearchDto>> searchActors(@RequestParam String query) {

       List<AttendanceActorSearchDto> list =  attendanceService.searchActors(query);

       return new ResponseEntity<>(list, HttpStatus.OK);
    }


}
