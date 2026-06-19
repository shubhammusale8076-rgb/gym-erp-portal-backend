package com.gym.Elite.Gym.attendanceEvent.controller;

import com.gym.Elite.Gym.attendanceEvent.dto.DeviceRegistrationRequest;
import com.gym.Elite.Gym.attendanceEvent.dto.DeviceResponse;
import com.gym.Elite.Gym.attendanceEvent.service.AttendanceDeviceService;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance/devices")
@RequiredArgsConstructor
public class AttendanceDeviceController {

    private final AttendanceDeviceService deviceService;

    @PostMapping
    public ResponseEntity<DeviceResponse> registerDevice(@Valid @RequestBody DeviceRegistrationRequest request) {
        return ResponseEntity.ok(deviceService.registerDevice(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> updateDevice(
            @PathVariable UUID id,
            @Valid @RequestBody DeviceRegistrationRequest request) {
        return ResponseEntity.ok(deviceService.updateDevice(id, request));
    }

    @PostMapping("/{id}/regenerate-api-key")
    public ResponseEntity<ResponseDto> updateAPIKey(@PathVariable UUID id){

        ResponseDto responseDto = deviceService.regenerateApiKey(id);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getDevices() {
        return ResponseEntity.ok(deviceService.getDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getDevice(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> setDeviceActive(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        deviceService.setDeviceActive(id, active);
        return ResponseEntity.ok().build();
    }
}
