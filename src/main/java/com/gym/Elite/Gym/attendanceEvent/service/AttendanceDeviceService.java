package com.gym.Elite.Gym.attendanceEvent.service;

import com.gym.Elite.Gym.attendanceEvent.dto.DeviceRegistrationRequest;
import com.gym.Elite.Gym.attendanceEvent.dto.DeviceResponse;
import com.gym.Elite.Gym.attendanceEvent.entity.AttendanceDevice;
import com.gym.Elite.Gym.attendanceEvent.exception.DeviceNotFoundException;
import com.gym.Elite.Gym.attendanceEvent.mapper.AttendanceMapper;
import com.gym.Elite.Gym.attendanceEvent.repo.AttendanceDeviceRepository;
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

        AttendanceDevice device = AttendanceDevice.builder()
                .tenantId(tenantId)
                .deviceName(request.getDeviceName())
                .deviceCode(request.getDeviceCode())
                .manufacturer(request.getManufacturer())
                .model(request.getModel())
                .ipAddress(request.getIpAddress())
                .port(request.getPort())
                .deviceType(request.getDeviceType())
                .apiKey(request.getApiKey())
                .active(true)
                .build();

        return mapper.toDeviceResponse(deviceRepo.save(device));
    }

    @Transactional
    public DeviceResponse updateDevice(UUID id, DeviceRegistrationRequest request) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        AttendanceDevice device = deviceRepo.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        device.setDeviceName(request.getDeviceName());
        device.setManufacturer(request.getManufacturer());
        device.setModel(request.getModel());
        device.setIpAddress(request.getIpAddress());
        device.setPort(request.getPort());
        device.setDeviceType(request.getDeviceType());
        
        if (request.getApiKey() != null && !request.getApiKey().isEmpty()) {
            device.setApiKey(request.getApiKey());
        }

        return mapper.toDeviceResponse(deviceRepo.save(device));
    }

    @Transactional
    public void setDeviceActive(UUID id, boolean active) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        AttendanceDevice device = deviceRepo.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new DeviceNotFoundException(id));
        
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

    @Transactional
    public void updateSyncTime(UUID deviceId) {
        deviceRepo.findById(deviceId).ifPresent(device -> {
            device.setLastSyncAt(LocalDateTime.now());
            deviceRepo.save(device);
        });
    }
}
