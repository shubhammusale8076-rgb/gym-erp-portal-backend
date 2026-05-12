package com.gym.Elite.Gym.attendanceEvent.integration.adapters;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;

/**
 * Contract that every device integration must implement.
 *
 * Architecture principle:
 *   Adding a new device type = implement this interface + register the Spring bean.
 *   AttendanceService NEVER changes.
 *
 * Raw payload type intentionally uses Object to allow any device format:
 *   - Map<String, Object> for JSON payloads
 *   - byte[] for binary protocols
 *   - Custom DTOs for vendor-specific formats
 *
 * All adapters must produce a standardized AttendanceEventDto.
 */
public interface AttendanceDeviceAdapter {

    /**
     * Convert the raw device payload into the normalized attendance event DTO.
     *
     * @param rawPayload The device-specific payload received at the integration endpoint
     * @return Normalized AttendanceEventDto ready for validation and processing
     */
    AttendanceEventDto convert(Object rawPayload);

    /**
     * The AttendanceSource this adapter handles.
     * Used by DeviceAdapterRegistry to route events without if/switch.
     */
    AttendanceSource getSupportedSource();
}
