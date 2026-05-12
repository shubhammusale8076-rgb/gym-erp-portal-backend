package com.gym.Elite.Gym.trainer.service;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceResponse;
import com.gym.Elite.Gym.attendanceEvent.dto.ManualAttendanceRequest;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @deprecated Legacy Trainer Attendance Service.
 * Functional logic has been migrated to the Unified Enterprise Attendance Platform (AttendanceService).
 * This class is kept for backward compatibility and will be removed in future releases.
 */
@Service
@RequiredArgsConstructor
@Deprecated
public class TrainerAttendanceService {

    private final AttendanceService unifiedAttendanceService;

    public AttendanceResponse checkIn(UUID trainerId, AttendanceSource source) {
        ManualAttendanceRequest request = new ManualAttendanceRequest();
        request.setActorId(trainerId);
        request.setActorType(AttendanceActorType.TRAINER);
        request.setSource(source);
        return unifiedAttendanceService.manualCheckIn(request);
    }

    public AttendanceResponse checkOut(UUID trainerId) {
        // Unified AttendanceService handles check-out via the same 'recordDeviceEvent' 
        // or a similar unified flow if exposed. 
        // For manual check-out, we can implement a manualCheckOut in AttendanceService.
        throw new UnsupportedOperationException("Manual check-out should be performed via Unified Attendance API");
    }
}
