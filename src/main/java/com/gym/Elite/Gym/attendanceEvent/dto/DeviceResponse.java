package com.gym.Elite.Gym.attendanceEvent.dto;

import com.gym.Elite.Gym.attendanceEvent.enums.DeviceType;
import com.gym.Elite.Gym.attendanceEvent.enums.DeviceVendor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for attendance device details.
 * API key is intentionally excluded from this response for security.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {

    private UUID id;
    private String deviceName;
    private String deviceCode;
    private DeviceVendor vendor;
    private String model;
    private Boolean active;
    private DeviceType deviceType;
    private LocalDateTime lastSyncAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
