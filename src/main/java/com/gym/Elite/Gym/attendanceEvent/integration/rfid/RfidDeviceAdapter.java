package com.gym.Elite.Gym.attendanceEvent.integration.rfid;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.integration.adapters.AttendanceDeviceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Adapter for RFID/Card swipe devices.
 */
@Component
@Slf4j
public class RfidDeviceAdapter implements AttendanceDeviceAdapter {

    @Override
    public AttendanceEventDto convert(Object rawPayload) {
        log.debug("Converting RFID raw payload: {}", rawPayload);
        
        if (rawPayload instanceof Map) {
            Map<?, ?> data = (Map<?, ?>) rawPayload;
            String cardId = (String) data.get("card_id");
            
            return AttendanceEventDto.builder()
                    .source(getSupportedSource())
                    .timestamp(LocalDateTime.now())
                    .verificationId(cardId)
                    .notes("Processed by RfidDeviceAdapter")
                    .build();
        }
        
        throw new IllegalArgumentException("Unsupported RFID payload format");
    }

    @Override
    public AttendanceSource getSupportedSource() {
        return AttendanceSource.RFID_CARD;
    }
}
