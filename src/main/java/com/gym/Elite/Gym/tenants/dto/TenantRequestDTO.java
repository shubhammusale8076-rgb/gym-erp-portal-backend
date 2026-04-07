package com.gym.Elite.Gym.tenants.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequestDTO {

    private String gymName;
    private String ownerName;
    private String email;
    private String phoneNumber;
    private String plan;
}
