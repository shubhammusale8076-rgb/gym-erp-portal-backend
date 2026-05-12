package com.gym.Elite.Gym.trainer.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.repo.MemberRepo;
import com.gym.Elite.Gym.trainer.dto.MemberAssignmentDTO;
import com.gym.Elite.Gym.trainer.dto.TrainerAssignmentDTO;
import com.gym.Elite.Gym.trainer.dto.TrainerNameList;
import com.gym.Elite.Gym.trainer.entity.Trainer;
import com.gym.Elite.Gym.trainer.entity.TrainerMemberAssignment;
import com.gym.Elite.Gym.trainer.repo.TrainerMemberAssignmentRepo;
import com.gym.Elite.Gym.trainer.repo.TrainerRepo;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerAssignmentService {


    private final MemberRepo memberRepo;
    private final TrainerRepo trainerRepo;
    private final TrainerMemberAssignmentRepo assignmentRepo;

    public List<MemberAssignmentDTO> getAllMembers() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<Member> members = memberRepo.findByTenantId(tenantId);

        return members.stream().map(member -> {

            Optional<TrainerMemberAssignment> assignment =
                    assignmentRepo.findByMemberIdAndTenantIdAndActiveTrue(member.getId(), tenantId);

            String planName = member.getSubscriptions()
                    .stream()
                    .findFirst()
                    .map(s -> s.getPlan().getName())
                    .orElse("N/A");

            return MemberAssignmentDTO.builder()
                    .id(member.getId())
                    .fullName(member.getFullName())
                    .email(member.getEmail())

                    .plan(planName) // 🔥 replace with actual later

                    .trainerId(assignment.map(a -> a.getTrainer().getId()).orElse(null))
                    .trainerName(assignment.map(a -> a.getTrainer().getFullName()).orElse("Unassigned"))

                    .status(member.getActive() ? "ACTIVE" : "INACTIVE")

                    .build();

        }).collect(Collectors.toList());
    }

    public List<TrainerNameList> getAllTrainer() {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        List<Trainer> trainerList = trainerRepo.findByTenantId(tenantId);

        return trainerList.stream().map( trainer -> TrainerNameList.builder()
                .id(trainer.getId())
                .name(trainer.getFullName())
                .build()).collect(Collectors.toList());
    }

    public TrainerAssignmentDTO getTrainerDetails(UUID trainerId) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Trainer trainer = trainerRepo.findByIdAndTenantId(trainerId, tenantId)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        long assignedCount = assignmentRepo
                .countByTrainerIdAndTenantIdAndActiveTrue(trainerId, tenantId);

        return TrainerAssignmentDTO.builder()
                .id(trainer.getId())
                .fullName(trainer.getFullName())
                .skills(trainer.getSkills())
                .available(trainer.getAvailable())

                .capacity(15) // 🔥 configurable later
                .assignedCount(assignedCount)

                .build();
    }

    public ResponseDto assignMembers(UUID trainerId, List<UUID> memberIds) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        Trainer trainer = getEntity(trainerId, tenantId);

        for (UUID memberId : memberIds) {

            // 🔥 deactivate existing assignment (if any)
            assignmentRepo.findByMemberIdAndTenantIdAndActiveTrue(memberId, tenantId)
                    .ifPresent(a -> {
                        a.setActive(false);
                        assignmentRepo.save(a);
                    });

            Member member = memberRepo.findByIdAndTenantId(memberId, tenantId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            TrainerMemberAssignment assignment = TrainerMemberAssignment.builder()
                    .trainer(trainer)
                    .member(member)
                    .tenantId(trainer.getTenantId())
                    .active(true)
                    .build();

            assignmentRepo.save(assignment);

        }
        return ResponseDto.builder()
                .code(200)
                .message("Members Assigned Successfully")
                .build();
    }

    public ResponseDto removeMember(UUID trainerId, UUID memberId) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        TrainerMemberAssignment assignment =
                assignmentRepo.findByTrainerIdAndMemberIdAndTenantIdAndActiveTrue(trainerId, memberId, tenantId)
                        .orElseThrow(() -> new RuntimeException("Assignment not found"));;

        assignment.setActive(false);
        assignmentRepo.save(assignment);
        return ResponseDto.builder()
                .code(200)
                .message("Member Removed Successfully")
                .build();
    }

    private Trainer getEntity(UUID trainerId, UUID tenantId) {
        return trainerRepo.findByIdAndTenantId(trainerId, tenantId)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
    }



}
