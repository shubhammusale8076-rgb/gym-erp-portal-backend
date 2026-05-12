package com.gym.Elite.Gym.attendanceEvent.enums;

/**
 * Lifecycle status for a member attendance record.
 *
 * Replaces the old anemic enum: PRESENT, ABSENT, IN_PROGRESS.
 *
 * DB migration renames existing values:
 *   PRESENT     → COMPLETED
 *   IN_PROGRESS → CHECKED_IN
 *   ABSENT      → REJECTED
 */
public enum AttendanceStatus {

    /** Member has checked in; session is currently open */
    CHECKED_IN,

    /** Member has explicitly checked out; session closed normally */
    CHECKED_OUT,

    /** Session fully completed (checked out, duration calculated) */
    COMPLETED,

    /** Processing failed (e.g., device error, system fault) */
    FAILED,

    /** Rejected because an identical / near-identical record already exists */
    DUPLICATE,

    /** Rejected by validation engine (expired membership, frozen, etc.) */
    REJECTED
}
