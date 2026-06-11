package com.huawei.opsfactory.common.config;

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
@Component
@ConfigurationProperties(prefix = "common.aop.machine")
public class CommonProperties {
    private String userName = "";
    private String password = "";

    /**
     * Gets the configured machine authentication username.
     *
     * @return the username, defaults to empty string if not configured
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the machine authentication username.
     *
     * @param userName the username to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the configured machine authentication password.
     *
     * @return the password, defaults to empty string if not configured
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the machine authentication password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
