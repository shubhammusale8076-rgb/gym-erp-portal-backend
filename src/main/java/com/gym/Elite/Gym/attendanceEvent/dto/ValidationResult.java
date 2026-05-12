package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result from AttendanceValidationService.
 *
 * Using a result object (not exceptions) for validation allows:
 * 1. Saving audit records for every rejection reason
 * 2. Returning machine-readable failure codes to the frontend
 * 3. Keeping AttendanceService free from try/catch validation noise
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    private boolean valid;

    /** Human-readable reason for failure, shown to frontend or logged to audit */
    private String failureReason;

    /** Machine-readable code for programmatic use (e.g., MEMBERSHIP_EXPIRED, ALREADY_CHECKED_IN) */
    private String failureCode;

    public static ValidationResult success() {
        return ValidationResult.builder().valid(true).build();
    }

    public static ValidationResult failure(String code, String reason) {
        return ValidationResult.builder()
                .valid(false)
                .failureCode(code)
                .failureReason(reason)
                .build();
    }
}
