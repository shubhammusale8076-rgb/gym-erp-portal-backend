package com.gym.Elite.Gym.attendanceEvent.controller;

import com.gym.Elite.Gym.attendanceEvent.dto.ActiveMemberAttendanceDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.HeatmapRequestDTO;
import com.gym.Elite.Gym.attendanceEvent.dto.HeatmapResponseDTO;
import com.gym.Elite.Gym.attendanceEvent.entity.Attendance;
import com.gym.Elite.Gym.attendanceEvent.service.AttendanceService;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/event")
    public ResponseEntity<ResponseDto> recordEvent(@RequestBody AttendanceEventDTO dto) {

        ResponseDto responseDto = attendanceService.recordEvent(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/active-members")
    public ResponseEntity<List<ActiveMemberAttendanceDTO>> getActiveMembers() {

        List<ActiveMemberAttendanceDTO> memberAttendanceDTOS = attendanceService.getTodayActiveMembers();
        return new ResponseEntity<>(memberAttendanceDTOS, HttpStatus.OK);
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<HeatmapResponseDTO>> getHeatmap(
            @RequestParam String memberId,
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        HeatmapRequestDTO request = new HeatmapRequestDTO();

        request.setMemberId(memberId);
        request.setRange(range);

        if (startDate != null && endDate != null) {
            request.setStartDate(LocalDate.parse(startDate));
            request.setEndDate(LocalDate.parse(endDate));
        }

        return ResponseEntity.ok(attendanceService.getHeatmap(request));
    }

}



