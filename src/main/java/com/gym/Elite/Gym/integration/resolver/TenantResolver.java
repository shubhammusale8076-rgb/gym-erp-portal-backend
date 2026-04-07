package com.gym.Elite.Gym.integration.resolver;

import com.gym.Elite.Gym.tenants.entity.Tenants;

public interface TenantResolver {
    String getProvider();
    Tenants resolve(String payload);
}
