package com.gym.Elite.Gym.attendanceEvent.dto;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.enums.DeviceType;
import com.gym.Elite.Gym.attendanceEvent.enums.DeviceVendor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for registering or updating an attendance device.
 *
 * Supports future expansion while keeping the setup simple for gym owners.
 */
@Data
public class DeviceRegistrationRequest {

    /**
     * Friendly display name.
     *
     * Examples:
     * Main Attendance Device
     * Reception Scanner
     * Front Desk Biometric
     */
    @NotBlank(message = "Device name is required")
    private String deviceName;

    /**
     * Unique code used internally for device identification.
     *
     * Examples:
     * MAIN_ENTRY_01
     * RECEPTION_BIO_01
     */
    @NotBlank(message = "Device code is required")
    private String deviceCode;

    @NotBlank(message = "Device Type is required")
    private DeviceType deviceType;

    /**
     * Device vendor/manufacturer.
     *
     * Examples:
     * ZKTECO
     * ESSL
     * MATRIX
     * REALTIME
     */
    @NotNull(message = "Device vendor is required")
    private DeviceVendor vendor;

    /**
     * Device model.
     *
     * Examples:
     * MB20
     * K40
     * UA760
     */
    @NotBlank(message = "Device model is required")
    private String model;

    /**
     * Physical location inside the gym.
     *
     * Examples:
     * Reception
     * Main Entrance
     * Front Desk
     */
    @NotBlank(message = "Device location is required")
    private String location;

    /**
     * Source used by attendance adapters.
     *
     * Examples:
     * BIOMETRIC
     * QR_SCANNER
     * RFID_CARD
     * FACE_RECOGNITION
     */
    @NotNull(message = "Attendance source is required")
    private AttendanceSource source;


}