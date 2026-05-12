package com.gym.Elite.Gym.attendanceEvent.controller;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceResponse;
import com.gym.Elite.Gym.attendanceEvent.dto.ManualAttendanceRequest;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AttendanceResponse> manualCheckIn(@Valid @RequestBody ManualAttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.manualCheckIn(request));
    }

    /**
     * Legacy convenience endpoint for member check-in.
     */
    @PostMapping("/member/{memberId}")
    public ResponseEntity<AttendanceResponse> memberCheckIn(@PathVariable UUID memberId) {
        ManualAttendanceRequest request = new ManualAttendanceRequest();
        request.setActorId(memberId);
        request.setActorType(AttendanceActorType.MEMBER);
        return ResponseEntity.ok(attendanceService.manualCheckIn(request));
    }

    /**
     * Legacy convenience endpoint for trainer check-in.
     */
    @PostMapping("/trainer/{trainerId}")
    public ResponseEntity<AttendanceResponse> trainerCheckIn(@PathVariable UUID trainerId) {
        ManualAttendanceRequest request = new ManualAttendanceRequest();
        request.setActorId(trainerId);
        request.setActorType(AttendanceActorType.TRAINER);
        return ResponseEntity.ok(attendanceService.manualCheckIn(request));
    }
}
