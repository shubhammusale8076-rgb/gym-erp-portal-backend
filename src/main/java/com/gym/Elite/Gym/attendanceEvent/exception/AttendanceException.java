package com.gym.Elite.Gym.attendanceEvent.exception;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus;

/**
 * Domain exception for all attendance business-rule violations.
 * Carries a machine-readable status so callers can decide how to respond
 * (e.g., save an audit record with DUPLICATE vs REJECTED).
 */
public class AttendanceException extends RuntimeException {

    private final AttendanceStatus attendanceStatus;

    public AttendanceException(String message, AttendanceStatus attendanceStatus) {
        super(message);
        this.attendanceStatus = attendanceStatus;
    }

    public AttendanceException(String message) {
        super(message);
        this.attendanceStatus = AttendanceStatus.FAILED;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }
}
