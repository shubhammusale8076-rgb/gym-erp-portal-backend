package com.gym.Elite.Gym.attendanceEvent.integration.adapters;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.exception.AttendanceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry that auto-discovers all AttendanceDeviceAdapter beans and routes
 * device events to the correct adapter by AttendanceSource — no if/switch needed.
 *
 * Spring will inject all implementations of AttendanceDeviceAdapter automatically.
 * When a new device type is added, just implement the interface; the registry
 * picks it up on startup with zero configuration.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceAdapterRegistry {

    private final List<AttendanceDeviceAdapter> adapters;

    private Map<AttendanceSource, AttendanceDeviceAdapter> adapterMap;

    @PostConstruct
    public void init() {
        adapterMap = adapters.stream()
                .collect(Collectors.toMap(
                        AttendanceDeviceAdapter::getSupportedSource,
                        Function.identity()
                ));

        log.info("DeviceAdapterRegistry initialized with {} adapters: {}",
                adapterMap.size(),
                adapterMap.keySet());
    }

    /**
     * Route and convert a raw payload from the given source to a normalized DTO.
     *
     * @param source     Attendance source (determines which adapter to use)
     * @param rawPayload Device-specific payload
     * @return Normalized AttendanceEventDto
     * @throws AttendanceException if no adapter is registered for this source
     */
    public AttendanceEventDto convert(AttendanceSource source, Object rawPayload) {
        AttendanceDeviceAdapter adapter = adapterMap.get(source);

        if (adapter == null) {
            throw new AttendanceException(
                    "No adapter registered for attendance source: " + source
            );
        }

        log.debug("Routing attendance event to {} adapter", source);
        return adapter.convert(rawPayload);
    }

    public boolean supportsSource(AttendanceSource source) {
        return adapterMap.containsKey(source);
    }
}
