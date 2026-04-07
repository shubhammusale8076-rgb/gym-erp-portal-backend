package com.gym.Elite.Gym.integration.resolver;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TenantResolverFactory {

    private final Map<String, TenantResolver> resolverMap = new HashMap<>();

    public TenantResolverFactory(List<TenantResolver> resolvers) {
        for (TenantResolver r : resolvers) {
            resolverMap.put(r.getProvider(), r);
        }
    }

    public TenantResolver get(String provider) {
        return resolverMap.get(provider.toLowerCase());
    }
}