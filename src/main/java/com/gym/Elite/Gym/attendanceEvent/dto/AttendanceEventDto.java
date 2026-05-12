package com.gym.Elite.Gym.attendanceEvent.dto;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Standardized event DTO consumed by ALL attendance sources.
 *
 * Now supports Actor Type Architecture:
 * - memberId replaced with actorId/actorCode/actorType.
 * - Allows biometric/RFID devices to send 'codes' which are resolved by the service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEventDto {

    /** Resolved actor UUID (if known by the source/adapter) */
    private UUID actorId;

    /** External code from device (e.g., Badge ID, Fingerprint template ID) */
    private String actorCode;

    /** Type of actor (Member, Trainer, Staff, etc.) */
    private AttendanceActorType actorType;

    /** Device UUID registered in attendance_devices table */
    private UUID deviceId;

    /** When the physical event occurred on the device */
    private LocalDateTime timestamp;

    /** Normalized source */
    private AttendanceSource source;

    private boolean success;

    private UUID attendanceId;

    /**
     * Opaque token from the device for de-duplication and audit.
     */
    private String verificationId;

    /** Optional notes */
    private String notes;
}