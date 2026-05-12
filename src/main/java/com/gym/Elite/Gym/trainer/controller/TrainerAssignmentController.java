package com.gym.Elite.Gym.trainer.controller;


import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.trainer.dto.AssignMembersRequest;
import com.gym.Elite.Gym.trainer.dto.MemberAssignmentDTO;
import com.gym.Elite.Gym.trainer.dto.TrainerAssignmentDTO;
import com.gym.Elite.Gym.trainer.dto.TrainerNameList;
import com.gym.Elite.Gym.trainer.service.TrainerAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignment")
@RequiredArgsConstructor
public class TrainerAssignmentController {


    private final TrainerAssignmentService trainerAssignmentService;


    @GetMapping("/get-members")
    public ResponseEntity<List<MemberAssignmentDTO>> getAllMembers() {
        List<MemberAssignmentDTO> memberAssignmentDTOList =  trainerAssignmentService.getAllMembers();
        return new ResponseEntity<>(memberAssignmentDTOList, HttpStatus.OK);

    }

    @GetMapping("/get-trainers")
    public ResponseEntity<List<TrainerNameList>> getAllTrainer() {
        List<TrainerNameList> memberAssignmentDTOList =  trainerAssignmentService.getAllTrainer();
        return new ResponseEntity<>(memberAssignmentDTOList, HttpStatus.OK);

    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<TrainerAssignmentDTO> getTrainerDetails(@PathVariable UUID trainerId) {
        TrainerAssignmentDTO dto =  trainerAssignmentService.getTrainerDetails(trainerId);
        return new ResponseEntity<>(dto, HttpStatus.OK);

    }


    @PostMapping("/assign-trainer")
    public ResponseEntity<ResponseDto> assignMembers(@RequestBody AssignMembersRequest request) {
        ResponseDto responseDto =  trainerAssignmentService.assignMembers(request.getTrainerId(), request.getMemberIds());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/remove-trainer/{trainerId}")
    public ResponseEntity<ResponseDto> removeMember(@PathVariable UUID trainerId, @RequestParam UUID memberId) {
        ResponseDto responseDto = trainerAssignmentService.removeMember(trainerId, memberId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
