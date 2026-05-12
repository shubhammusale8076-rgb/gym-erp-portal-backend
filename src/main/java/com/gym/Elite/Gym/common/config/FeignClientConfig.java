package com.gym.Elite.Gym.common.config;

import com.gym.Elite.Gym.crm.util.CorrelationIdFilter;
import com.gym.Elite.Gym.utility.SecurityUtils;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@Slf4j
public class FeignClientConfig {

    @Value("${internal.api.secret:}")
    private String internalSecret;

    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {
            // Propagate Correlation ID
            String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_LOG_VAR);
            if (correlationId != null) {
                requestTemplate.header(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
            }

            UUID tenantId = null;
            try {
                tenantId = SecurityUtils.getCurrentTenantId();
            } catch (Exception e) {
                // Background tasks might not have a security context
            }

            // =====================================
            // INTERNAL SECURITY
            // =====================================
            if (internalSecret != null && !internalSecret.isEmpty()) {
                requestTemplate.header(
                        "X-Internal-Secret",
                        internalSecret
                );
            }

            // =====================================
            // TENANT CONTEXT
            // =====================================
            if (tenantId != null) {
                requestTemplate.header(
                        "X-Tenant-Id",
                        tenantId.toString()
                );
            }

            log.debug(
                    "Feign request headers added for tenant: {} and correlationId: {}",
                    tenantId, correlationId
            );
        };
    }
}

