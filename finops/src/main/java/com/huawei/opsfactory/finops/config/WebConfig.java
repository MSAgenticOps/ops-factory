package com.huawei.opsfactory.finops.config;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures HTTP behavior for the FinOps API.
 *
 * @since 2026-05-28
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);
    private final FinOpsProperties properties;

    public WebConfig(FinOpsProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/finops/**")
            .allowedOrigins(resolveAllowedOrigins())
            .allowedMethods("GET", "POST", "OPTIONS")
            .allowedHeaders("content-type", "x-secret-key");
    }

    private String[] resolveAllowedOrigins() {
        Set<String> origins = new LinkedHashSet<>();
        String configuredOrigin = properties.getCorsOrigin();
        if (configuredOrigin == null || configuredOrigin.isBlank()) {
            return new String[0];
        }
        origins.add(configuredOrigin);
        try {
            URI uri = URI.create(configuredOrigin);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            int port = uri.getPort();
            if ("127.0.0.1".equals(host) || "localhost".equals(host)) {
                origins.add(buildOrigin(scheme, "127.0.0.1", port));
                origins.add(buildOrigin(scheme, "localhost", port));
            }
        } catch (IllegalArgumentException ex) {
            log.warn("Ignoring invalid FinOps CORS origin", ex);
        }
        return origins.toArray(String[]::new);
    }

    private static String buildOrigin(String scheme, String host, int port) {
        return port > 0 ? scheme + "://" + host + ":" + port : scheme + "://" + host;
    }
}
