package com.gym.Elite.Gym.common.config;

import com.gym.Elite.Gym.utility.SecurityUtils;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@Slf4j
public class FeignClientConfig {

    @Value("${internal.api.secret}")
    private String internalSecret;

    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {

            UUID tenantId = SecurityUtils.getCurrentTenantId();

            // =====================================
            // INTERNAL SECURITY
            // =====================================
            requestTemplate.header(
                    "X-Internal-Secret",
                    internalSecret
            );

            // =====================================
            // TENANT CONTEXT
            // =====================================
            if (tenantId != null) {

                requestTemplate.header(
                        "X-Tenant-Id",
                        tenantId.toString()
                );
            }

            log.info(
                    "Feign request headers added for tenant: {}",
                    tenantId
            );
        };
    }
}

