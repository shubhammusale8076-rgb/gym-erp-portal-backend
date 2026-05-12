package com.gym.Elite.Gym.attendanceEvent.enums;

/**
 * Physical device category registered in the system.
 * Determines which DeviceAdapter is used during event processing.
 */
public enum DeviceType {

    BIOMETRIC,
    RFID,
    QR_SCANNER,
    FACE_SCANNER,
    MOBILE
}
