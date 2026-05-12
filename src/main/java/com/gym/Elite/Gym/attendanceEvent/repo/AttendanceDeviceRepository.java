package com.gym.Elite.Gym.attendanceEvent.repo;

import com.gym.Elite.Gym.attendanceEvent.entity.AttendanceDevice;
import com.gym.Elite.Gym.attendanceEvent.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceDeviceRepository extends JpaRepository<AttendanceDevice, UUID> {

    List<AttendanceDevice> findAllByTenantId(UUID tenantId);

    List<AttendanceDevice> findAllByTenantIdAndActiveTrue(UUID tenantId);

    List<AttendanceDevice> findAllByTenantIdAndDeviceType(UUID tenantId, DeviceType deviceType);

    Optional<AttendanceDevice> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<AttendanceDevice> findByDeviceCodeAndTenantId(String deviceCode, UUID tenantId);

    boolean existsByDeviceCodeAndTenantId(String deviceCode, UUID tenantId);
}
