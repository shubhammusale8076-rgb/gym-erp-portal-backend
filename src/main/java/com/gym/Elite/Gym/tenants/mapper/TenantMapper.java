package com.gym.Elite.Gym.tenants.mapper;

import com.gym.Elite.Gym.tenants.dto.TenantDetailsDTO;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {

    public TenantDetailsDTO mapToDTO(Tenants tenant) {
        return TenantDetailsDTO.builder()
                .id(tenant.getId())
                .gymName(tenant.getGymName())
                .ownerName(tenant.getOwnerName())
                .email(tenant.getEmail())
                .phoneNumber(tenant.getPhoneNumber())
                .plan(tenant.getPlan())
                .status(tenant.isStatus()) // Changed from getStatus() to isStatus() for boolean
                .createdOn(tenant.getCreatedOn())
                .build();
    }
}
