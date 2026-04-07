package com.gym.Elite.Gym.tenants.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.tenants.dto.TenantDetailsDTO;
import com.gym.Elite.Gym.tenants.dto.TenantRequestDTO;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.tenants.mapper.TenantMapper;
import com.gym.Elite.Gym.tenants.repo.TenantRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepo tenantRepo;
    private final TenantMapper tenantMapper;

    public ResponseDto createTenant(TenantRequestDTO request) {

        if (tenantRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Tenant with this email already exists");
        }

        Tenants tenant = Tenants.builder()
                .gymName(request.getGymName())
                .ownerName(request.getOwnerName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .plan(request.getPlan())
                .status(true)
                .createdOn(new Date())
                .build();

        tenantRepo.save(tenant);

        return ResponseDto.builder().code(201).message("Tenant Added Successfully").build();
    }

    public TenantDetailsDTO getTenantById(UUID id) {
        Tenants tenant = tenantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        return tenantMapper.mapToDTO(tenant);
    }

    public List<TenantDetailsDTO> getAllTenants() {
        return tenantRepo.findAll()
                .stream()
                .map(tenantMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public ResponseDto updateTenant(UUID id, TenantRequestDTO request) {

        Tenants tenant = tenantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        tenant.setGymName(request.getGymName());
        tenant.setOwnerName(request.getOwnerName());
        tenant.setEmail(request.getEmail());
        tenant.setPhoneNumber(request.getPhoneNumber());
        tenant.setPlan(request.getPlan());

        tenantRepo.save(tenant);
        return ResponseDto.builder().code(201).message("Tenant Updated Successfully").build();
    }

    public ResponseDto deleteTenant(UUID id) {

        Tenants tenant = tenantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        tenantRepo.delete(tenant);

        return ResponseDto.builder().code(201).message("Tenant Deleted Successfully").build();

    }

    public ResponseDto activateTenant(UUID id) {
        Tenants tenant = tenantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        tenant.setStatus(true);
        tenantRepo.save(tenant);
        return ResponseDto.builder().code(201).message("Tenant Activated Successfully").build();

    }

    public ResponseDto deactivateTenant(UUID id) {
        Tenants tenant = tenantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        tenant.setStatus(false);
        tenantRepo.save(tenant);
        return ResponseDto.builder().code(201).message("Tenant De-Activated Successfully").build();

    }
}
