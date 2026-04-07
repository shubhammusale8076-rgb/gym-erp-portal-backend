package com.gym.Elite.Gym.tenants.repo;

import com.gym.Elite.Gym.tenants.entity.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenantRepo extends JpaRepository<Tenants, UUID> {

    boolean existsByEmail(String email);
}
