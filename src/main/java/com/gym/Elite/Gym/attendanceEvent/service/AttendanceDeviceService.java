package com.gym.Elite.Gym.attendanceEvent.service;

import com.gym.Elite.Gym.attendanceEvent.dto.DeviceRegistrationRequest;
import com.gym.Elite.Gym.attendanceEvent.dto.DeviceResponse;
import com.gym.Elite.Gym.attendanceEvent.entity.AttendanceDevice;
import com.gym.Elite.Gym.attendanceEvent.enums.DeviceStatus;
import com.gym.Elite.Gym.attendanceEvent.exception.AttendanceException;
import com.gym.Elite.Gym.attendanceEvent.exception.DeviceNotFoundException;
import com.gym.Elite.Gym.attendanceEvent.mapper.AttendanceMapper;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceDeviceRepository;
import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.utility.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceDeviceService {

    private final AttendanceDeviceRepository deviceRepo;
    private final AttendanceMapper mapper;

    @Transactional
    public DeviceResponse registerDevice(DeviceRegistrationRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        
        if (deviceRepo.existsByDeviceCodeAndTenantId(request.getDeviceCode(), tenantId)) {
            throw new RuntimeException("Device code already registered for this tenant");
        }

        String apiKey = UUID.randomUUID().toString();

        AttendanceDevice device = AttendanceDevice.builder()
                .tenantId(tenantId)
                .deviceName(request.getDeviceName())
                .deviceCode(request.getDeviceCode())
                .deviceType(request.getDeviceType())
                .vendor(request.getVendor())
                .model(request.getModel())
                .source(request.getSource())
                .apiKey(apiKey)
                .active(false)
                .status(DeviceStatus.NEVER_CONNECTED)
                .build();

        return mapper.toDeviceResponse(deviceRepo.save(device));
    }

    @Transactional
    public DeviceResponse updateDevice(UUID id, DeviceRegistrationRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        AttendanceDevice device = deviceRepo.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        device.setDeviceName(request.getDeviceName());
        device.setVendor(request.getVendor());
        device.setModel(request.getModel());
        device.setVendor(request.getVendor());
        device.setSource(request.getSource());
        device.setDeviceType(request.getDeviceType());

        return mapper.toDeviceResponse(deviceRepo.save(device));
    }

    @Transactional
    public ResponseDto regenerateApiKey(UUID id) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        AttendanceDevice device = deviceRepo.findByIdAndTenantId(id, tenantId)
                        .orElseThrow(() -> new DeviceNotFoundException(id));

        device.setApiKey(UUID.randomUUID().toString());

        deviceRepo.save(device);
        return ResponseDto.builder().code(201).message("API Key updated Successfully").build();
    }

    @Transactional
    public void setDeviceActive(UUID id, boolean active) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();

        AttendanceDevice device = deviceRepo.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        if (active) {

            deviceRepo.findByTenantIdAndActiveTrue(tenantId)
                    .ifPresent(existing -> {

                        if (!existing.getId().equals(id)) {
                            throw new RuntimeException(
                                    "Another active attendance device already exists");
                        }
                    });
        }
        
        device.setActive(active);
        deviceRepo.save(device);
    }

    public List<DeviceResponse> getDevices() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return deviceRepo.findAllByTenantId(tenantId).stream()
                .map(mapper::toDeviceResponse)
                .collect(Collectors.toList());
    }

    public DeviceResponse getDeviceById(UUID id) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return deviceRepo.findByIdAndTenantId(id, tenantId)
                .map(mapper::toDeviceResponse)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    public AttendanceDevice validateAndUpdateHeartbeat( String deviceCode, String apiKey) {

        UUID tenantId = SecurityUtils.getCurrentTenantId();
                AttendanceDevice device = deviceRepo.findByDeviceCodeAndTenantId(deviceCode, tenantId)
                        .orElseThrow(() -> new AttendanceException("Device not found"));

        if (!device.getStatus().equals(DeviceStatus.ACTIVE)) {
            throw new AttendanceException("Device inactive");
        }

        if (!device.getApiKey().equals(apiKey)) {
            throw new AttendanceException("Invalid API key");
        }

        device.setStatus(DeviceStatus.CONNECTED);
        device.setLastHeartbeat(LocalDateTime.now());


        deviceRepo.save(device);

        return device;
    }

    @Transactional
    public void updateSyncTime(UUID deviceId) {
        deviceRepo.findById(deviceId).ifPresent(device -> {
            device.setLastSyncAt(LocalDateTime.now());
            deviceRepo.save(device);
        });
    }
}
