package com.gym.Elite.Gym.attendanceEvent.integration.biometric;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.integration.adapters.AttendanceDeviceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Adapter for Biometric devices (e.g., ZKTeco, eSSL).
 * Converts raw payloads (usually Map or JSON) from biometric devices into normalized events.
 */
@Component
@Slf4j
public class BiometricDeviceAdapter implements AttendanceDeviceAdapter {

    @Override
    public AttendanceEventDto convert(Object rawPayload) {
        log.debug("Converting biometric raw payload: {}", rawPayload);
        
        // Mock implementation for generic biometric payload
        if (rawPayload instanceof Map) {
            Map<?, ?> data = (Map<?, ?>) rawPayload;
            String externalId = (String) data.get("user_id");
            String deviceCode = (String) data.get("device_code");
            
            return AttendanceEventDto.builder()
                    .source(getSupportedSource())
                    .timestamp(LocalDateTime.now()) // Use server time if device time is missing
                    .verificationId(externalId)
                    .notes("Processed by BiometricDeviceAdapter")
                    .build();
        }
        
        throw new IllegalArgumentException("Unsupported biometric payload format");
    }

    @Override
    public AttendanceSource getSupportedSource() {
        return AttendanceSource.BIOMETRIC;
    }
}
