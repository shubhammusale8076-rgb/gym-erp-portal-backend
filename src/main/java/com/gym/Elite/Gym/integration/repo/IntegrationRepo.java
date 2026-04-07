package com.gym.Elite.Gym.integration.repo;

import com.gym.Elite.Gym.integration.entity.Integration;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntegrationRepo extends JpaRepository<Integration, UUID> {
    Optional<Integration> findByTenantAndProvider(Tenants tenant, String provider);

    Optional<Integration> findByProviderAndPhoneNumberId(String whatsapp, String phoneNumberId);
}
