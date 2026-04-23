package com.gym.Elite.Gym.tenants.controller;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.tenants.dto.TenantDetailsDTO;
import com.gym.Elite.Gym.tenants.dto.TenantRequestDTO;
import com.gym.Elite.Gym.tenants.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/add-tenant")
    public ResponseEntity<ResponseDto> createTenant(@RequestBody TenantRequestDTO request) {
        ResponseDto responseDto = tenantService.createTenant(request);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/get-tenant/{id}")
    public ResponseEntity<TenantDetailsDTO> getTenant(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    @GetMapping("/get-all-tenants")
    public ResponseEntity<List<TenantDetailsDTO>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @PutMapping("/update-tenant/{id}")
    public ResponseEntity<ResponseDto> updateTenant(@PathVariable UUID id, @RequestBody TenantRequestDTO request) {

        return ResponseEntity.ok(tenantService.updateTenant(id, request));
    }

    @DeleteMapping("/delete-tenant/{id}")
    public ResponseEntity<ResponseDto> deleteTenant(@PathVariable UUID id) {
        ResponseDto responseDto=  tenantService.deleteTenant(id);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ResponseDto> activateTenant(@PathVariable UUID id) {
        ResponseDto responseDto = tenantService.activateTenant(id);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResponseDto> deactivateTenant(@PathVariable UUID id) {
        ResponseDto responseDto = tenantService.deactivateTenant(id);
        return ResponseEntity.ok(responseDto);
    }


}
