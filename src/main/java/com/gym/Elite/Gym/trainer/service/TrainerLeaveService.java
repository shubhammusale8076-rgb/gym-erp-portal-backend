package com.gym.Elite.Gym.trainer.service;

import com.gym.Elite.Gym.trainer.dto.TrainerLeaveRequestDTO;
import com.gym.Elite.Gym.trainer.dto.TrainerLeaveResponseDTO;
import com.gym.Elite.Gym.trainer.entity.*;
import com.gym.Elite.Gym.trainer.mapper.TrainerLeaveMapper;
import com.gym.Elite.Gym.trainer.repo.TrainerAttendanceRepository;
import com.gym.Elite.Gym.trainer.repo.TrainerLeaveRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerLeaveService {

    private final TrainerLeaveRepository leaveRepo;
    private final TrainerAttendanceRepository attendanceRepo;
    private final TrainerLeaveMapper mapper;

    @Transactional
    public TrainerLeaveResponseDTO applyLeave(UUID trainerId, TrainerLeaveRequestDTO request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        // Check for overlapping leaves
        if (!leaveRepo.findOverlappingLeaves(trainerId, tenantId, request.getStartDate(), request.getEndDate()).isEmpty()) {
            throw new RuntimeException("Leave period overlaps with existing leave application");
        }

        TrainerLeave leave = TrainerLeave.builder()
                .trainerId(trainerId)
                .tenantId(tenantId)
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        return mapper.toResponseDTO(leaveRepo.save(leave));
    }

    @Transactional
    public TrainerLeaveResponseDTO approveLeave(UUID leaveId, String comments) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        TrainerLeave leave = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found"));

        if (!leave.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied");
        }

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setComments(comments);
        // In a real scenario, approvedBy would be the logged-in user ID.
        // For now, I'll just set it to a dummy value or leave it null if not provided.

        // Mark attendance as ON_LEAVE for the period
        markAttendanceOnLeave(leave);

        return mapper.toResponseDTO(leaveRepo.save(leave));
    }

    @Transactional
    public TrainerLeaveResponseDTO rejectLeave(UUID leaveId, String comments) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        TrainerLeave leave = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found"));

        if (!leave.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setComments(comments);
        return mapper.toResponseDTO(leaveRepo.save(leave));
    }

    public List<TrainerLeaveResponseDTO> getTrainerLeaves(UUID trainerId) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return leaveRepo.findAllByTrainerIdAndTenantId(trainerId, tenantId)
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void markAttendanceOnLeave(TrainerLeave leave) {
        LocalDate current = leave.getStartDate();
        while (!current.isAfter(leave.getEndDate())) {
            final LocalDate date = current;
            attendanceRepo.findByTrainerIdAndDateAndTenantId(leave.getTrainerId(), date, leave.getTenantId())
                    .ifPresentOrElse(
                        a -> {
                            a.setStatus(TrainerAttendanceStatus.ON_LEAVE);
                            attendanceRepo.save(a);
                        },
                        () -> {
                            TrainerAttendance a = TrainerAttendance.builder()
                                    .trainerId(leave.getTrainerId())
                                    .tenantId(leave.getTenantId())
                                    .date(date)
                                    .status(TrainerAttendanceStatus.ON_LEAVE)
                                    .build();
                            attendanceRepo.save(a);
                        }
                    );
            current = current.plusDays(1);
        }
    }
}
