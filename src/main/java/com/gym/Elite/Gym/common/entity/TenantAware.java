package com.gym.Elite.Gym.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

/**
 * Base class for all tenant-scoped entities.
 * Provides a plain {@code tenantId} UUID column with no FK constraint,
 * enabling loose coupling from the Tenants entity.
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class TenantAware {

    @Column(name = "tenant_id", nullable = false)
    protected UUID tenantId;
}
