package com.gym.Elite.Gym.attendanceEvent.integration.qr;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.integration.adapters.AttendanceDeviceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Adapter for QR code scanners.
 */
@Component
@Slf4j
public class QrDeviceAdapter implements AttendanceDeviceAdapter {

    @Override
    public AttendanceEventDto convert(Object rawPayload) {
        log.debug("Converting QR raw payload: {}", rawPayload);
        
        if (rawPayload instanceof Map) {
            Map<?, ?> data = (Map<?, ?>) rawPayload;
            String qrToken = (String) data.get("qr_token");
            
            return AttendanceEventDto.builder()
                    .source(getSupportedSource())
                    .timestamp(LocalDateTime.now())
                    .verificationId(qrToken)
                    .notes("Processed by QrDeviceAdapter")
                    .build();
        }
        
        throw new IllegalArgumentException("Unsupported QR payload format");
    }

    @Override
    public AttendanceSource getSupportedSource() {
        return AttendanceSource.QR_CODE;
    }
}
