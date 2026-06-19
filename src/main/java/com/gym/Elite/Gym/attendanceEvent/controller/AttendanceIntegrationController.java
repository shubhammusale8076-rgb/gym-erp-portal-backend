package com.gym.Elite.Gym.attendanceEvent.controller;

import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventDto;
import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceEventResponseDto;
import com.gym.Elite.Gym.attendanceEvent.dto.AttendanceResponse;
import com.gym.Elite.Gym.attendanceEvent.entity.AttendanceDevice;
import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceSource;
import com.gym.Elite.Gym.attendanceEvent.enums.DeviceStatus;
import com.gym.Elite.Gym.attendanceEvent.integration.adapters.DeviceAdapterRegistry;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceDeviceRepository;
import com.gym.Elite.Gym.attendanceEvent.service.AttendanceDeviceService;
import com.gym.Elite.Gym.attendanceEvent.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Public/Internal endpoint for device integrations.
 *
 * This controller handles the raw signals from different devices.
 * It uses the DeviceAdapterRegistry to normalize the payload before
 * passing it to the business logic.
 */
@RestController
@RequestMapping("/api/attendance/webhook")
@RequiredArgsConstructor
@Slf4j
public class AttendanceIntegrationController {

    private final DeviceAdapterRegistry adapterRegistry;
    private final AttendanceService attendanceService;
    private final AttendanceDeviceService attendanceDeviceService;

    /**
     * Unified entry point for all device sources.
     *
     * Example:
     *   POST /api/attendance/webhook/BIOMETRIC
     *   POST /api/attendance/webhook/RFID_CARD
     *
     * In production, this endpoint would be protected by API Key / OAuth
     * validated in a Security Filter or against the device record.
     */
    @PostMapping("/{source}")
    public ResponseEntity<AttendanceEventResponseDto> handleDeviceEvent(
            @PathVariable AttendanceSource source,
            @RequestHeader("X-DEVICE-CODE") String deviceCode,
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestBody Map<String, Object> rawPayload) {

        log.info("Received raw attendance event from source: {}", source);

        AttendanceDevice device = attendanceDeviceService.validateAndUpdateHeartbeat( deviceCode, apiKey);

        // 1. Normalize payload using the correct adapter
        AttendanceEventDto eventDto = adapterRegistry.convert(source, rawPayload);

        eventDto.setDeviceId(device.getId());

        // 2. Process through core business logic
        AttendanceEventResponseDto response = attendanceService.recordAttendanceEvent(eventDto);

        return ResponseEntity.ok(response);
    }
}
