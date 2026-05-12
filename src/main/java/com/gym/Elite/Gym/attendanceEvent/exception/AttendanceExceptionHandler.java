package com.gym.Elite.Gym.attendanceEvent.exception;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AttendanceExceptionHandler {

    @ExceptionHandler(AttendanceException.class)
    public ResponseEntity<AttendanceErrorResponse> handleAttendanceException(AttendanceException ex) {
        AttendanceErrorResponse response = AttendanceErrorResponse.builder()
                .message(ex.getMessage())
                .status(ex.getAttendanceStatus())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<AttendanceErrorResponse> handleDeviceNotFoundException(DeviceNotFoundException ex) {
        AttendanceErrorResponse response = AttendanceErrorResponse.builder()
                .message(ex.getMessage())
                .status(AttendanceStatus.FAILED)
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @Data
    @Builder
    public static class AttendanceErrorResponse {
        private String message;
        private AttendanceStatus status;
        private LocalDateTime timestamp;
    }
}
