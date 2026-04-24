package com.gym.Elite.Gym.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantRefRequest {

    private UUID tenantId;
    private String name;
    private String status;    // ACTIVE | SUSPENDED
    private String planCode;  // BASIC | PRO | ENTERPRISE
}
