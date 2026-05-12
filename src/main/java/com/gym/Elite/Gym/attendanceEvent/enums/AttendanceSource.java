package com.gym.Elite.Gym.attendanceEvent.enums;

/**
 * Unified attendance source enum for ALL attendance modules (member + trainer).
 * Replaces the two duplicate AttendanceSource enums that existed in
 * auth.entity and trainer.entity packages.
 *
 * Adding a new device type = add an entry here + a new DeviceAdapter implementation.
 * No changes required to AttendanceService or any core business logic.
 */
public enum AttendanceSource {

    /** Manually marked by staff/admin from the dashboard */
    MANUAL,

    /** Fingerprint / iris / vein biometric device */
    BIOMETRIC,

    /** RFID card / fob swipe */
    RFID_CARD,

    /** QR code scan (mobile or fixed scanner) */
    QR_CODE,

    /** Member self-check-in via mobile application */
    MOBILE_APP,

    /** Facial recognition device (future support) */
    FACE_RECOGNITION,

    /** Programmatic check-in via external API integration */
    API,

    /** Bulk historical data import */
    IMPORT
}
