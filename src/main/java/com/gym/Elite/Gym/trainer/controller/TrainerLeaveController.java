package com.gym.Elite.Gym.trainer.controller;

import com.gym.Elite.Gym.trainer.dto.TrainerLeaveResponseDTO;
import com.gym.Elite.Gym.trainer.service.TrainerLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class TrainerLeaveController {

    private final TrainerLeaveService leaveService;

    @PutMapping("/{leaveId}/approve")
    public ResponseEntity<TrainerLeaveResponseDTO> approveLeave(@PathVariable UUID leaveId, @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(leaveService.approveLeave(leaveId, comments));
    }

    @PutMapping("/{leaveId}/reject")
    public ResponseEntity<TrainerLeaveResponseDTO> rejectLeave(@PathVariable UUID leaveId, @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(leaveService.rejectLeave(leaveId, comments));
    }
}
