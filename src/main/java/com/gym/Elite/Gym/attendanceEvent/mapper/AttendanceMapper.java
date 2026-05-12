package com.gym.Elite.Gym.attendanceEvent.mapper;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceResponse;
import com.gym.Elite.Gym.attendanceEvent.dto.DeviceResponse;
import com.gym.Elite.Gym.attendanceEvent.entity.Attendance;
import com.gym.Elite.Gym.attendanceEvent.entity.AttendanceDevice;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    /**
     * Map Attendance entity to Response.
     * Note: actorName must be provided externally (from Resolver) as the entity 
     * no longer has a direct relationship to Member/Trainer.
     */
    public AttendanceResponse toResponse(Attendance attendance, String actorName) {
        if (attendance == null) return null;

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .actorId(attendance.getActorId())
                .actorType(attendance.getActorType())
                .actorName(actorName)
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .totalDurationMinutes(attendance.getTotalDurationMinutes())
                .status(attendance.getStatus())
                .source(attendance.getSource())
                .deviceId(attendance.getDeviceId())
                .deviceName(attendance.getDeviceName())
                .verificationId(attendance.getVerificationId())
                .verified(attendance.getVerified())
                .sessionType(attendance.getSessionType())
                .classId(attendance.getClassId())
                .notes(attendance.getNotes())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }

    public DeviceResponse toDeviceResponse(AttendanceDevice device) {
        if (device == null) return null;

        return DeviceResponse.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .deviceCode(device.getDeviceCode())
                .manufacturer(device.getManufacturer())
                .model(device.getModel())
                .ipAddress(device.getIpAddress())
                .port(device.getPort())
                .active(device.getActive())
                .deviceType(device.getDeviceType())
                .lastSyncAt(device.getLastSyncAt())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .build();
    }
}
