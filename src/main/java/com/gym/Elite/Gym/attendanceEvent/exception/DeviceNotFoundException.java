package com.gym.Elite.Gym.attendanceEvent.exception;

/**
 * Thrown when a requested AttendanceDevice does not exist or is not
 * accessible within the current tenant scope.
 */
public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException(java.util.UUID deviceId) {
        super("Device not found: " + deviceId);
    }
}
