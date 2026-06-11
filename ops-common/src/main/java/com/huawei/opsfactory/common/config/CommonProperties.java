package com.huawei.opsfactory.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for common AOP machine authentication.
 * <p>
 * Binds properties with the prefix {@code common.aop.machine} to provide
 * username and password credentials used by the {@link com.huawei.opsfactory.common.aop.BasicAuthAspect}.
 *
 * @since 2026-06-08
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "common.aop.machine")
public class CommonProperties {
    private String userName = "";
    private String password = "";

    @Override
    public String toString() {
        return "CommonProperties{userName= '" + userName + "', password= '******'}";
    }
}
