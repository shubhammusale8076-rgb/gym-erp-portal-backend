package com.gym.Elite.Gym.config;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception: ", e);
        return new ResponseEntity<>(
                ResponseDto.builder()
                        .code(500)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleGeneralException(Exception e) {
        log.error("General exception: ", e);
        return new ResponseEntity<>(
                ResponseDto.builder()
                        .code(500)
                        .message("An unexpected error occurred")
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
