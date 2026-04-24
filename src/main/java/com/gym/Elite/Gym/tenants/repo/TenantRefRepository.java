package com.gym.Elite.Gym.tenants.repo;

import com.gym.Elite.Gym.tenants.entity.TenantRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenantRefRepository extends JpaRepository<TenantRef, UUID> {
}
