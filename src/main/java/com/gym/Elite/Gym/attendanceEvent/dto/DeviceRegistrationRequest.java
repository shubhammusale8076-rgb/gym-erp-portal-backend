package com.gym.Elite.Gym.attendanceEvent.dto;

import com.gym.Elite.Gym.attendanceEvent.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for registering or updating an attendance device.
 * Used at POST /api/devices and PUT /api/devices/{id}
 */
@Data
public class DeviceRegistrationRequest {

    @NotBlank(message = "Device name is required")
    private String deviceName;

    @NotBlank(message = "Device code is required")
    private String deviceCode;

    private String manufacturer;

    private String model;

    private String ipAddress;

    private Integer port;

    @NotNull(message = "Device type is required")
    private DeviceType deviceType;

    /**
     * API key the device will use for authentication.
     * In production this should be hashed before storage.
     */
    private String apiKey;
}
