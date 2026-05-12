package com.gym.Elite.Gym.attendanceEvent.integration.mobile;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.integration.adapters.AttendanceDeviceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Adapter for mobile app check-ins.
 */
@Component
@Slf4j
public class MobileDeviceAdapter implements AttendanceDeviceAdapter {

    @Override
    public AttendanceEventDto convert(Object rawPayload) {
        log.debug("Converting Mobile raw payload: {}", rawPayload);
        
        if (rawPayload instanceof Map) {
            Map<?, ?> data = (Map<?, ?>) rawPayload;
            String memberIdStr = (String) data.get("member_id");
            
            return AttendanceEventDto.builder()
                    .source(getSupportedSource())
                    .timestamp(LocalDateTime.now())
                    .verificationId("MOBILE-" + memberIdStr)
                    .notes("Processed by MobileDeviceAdapter")
                    .build();
        }
        
        throw new IllegalArgumentException("Unsupported Mobile payload format");
    }

    @Override
    public AttendanceSource getSupportedSource() {
        return AttendanceSource.MOBILE_APP;
    }
}
