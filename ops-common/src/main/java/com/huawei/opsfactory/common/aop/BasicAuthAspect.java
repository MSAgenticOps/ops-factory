/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.aop;

import com.huawei.opsfactory.common.exception.ApiCallException;
import com.huawei.opsfactory.common.exception.AuthException;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.swagger.invocation.context.ContextUtils;
import org.apache.servicecomb.swagger.invocation.context.InvocationContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Basic authentication aspect for validating credentials.
 * <p>
 * This aspect intercepts methods annotated with {@link BasicAuth} and validates
 * the provided Basic authentication credentials against configured username and password.
 *
 * @since 2026-06-06
 */
@Aspect
@Component
@PropertySource(value = "classpath:ops-common-default.properties", ignoreResourceNotFound = true)
public class BasicAuthAspect {
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthAspect.class);

    @Value("${common.aop.machine.username}")
    private String configUserName;

    @Value("${common.aop.machine.password}")
    private String configPassword;

    /**
     * Basic authentication advice.
     * <p>
     * Validates Basic authentication credentials from request headers or invocation context.
     * Supports both method-level and class-level {@link BasicAuth} annotations.
     *
     * @param pjp the proceeding join point
     * @return the result of method execution
     * @throws Throwable if authentication fails or an error occurs
     */
    @Around("@annotation(com.huawei.opsfactory.common.aop.BasicAuth) || @within(com.huawei.opsfactory.common.aop.BasicAuth)")
    public Object basicAuth(ProceedingJoinPoint pjp) throws Throwable {
        logger.info("BasicAuthAspect triggered for method: {}", pjp.getSignature().getName());
        String authHeader;
        HttpServletRequest request = getCurrentRequest();

        // 从请求头中获取Authorization
        try {
            authHeader = request.getHeader("Authorization");
        } catch (Exception e) {
            logger.debug("Failed to get Authorization header from request", e);
            authHeader = "";
        }

        // 从上文获取
        try {
            if (StringUtils.isEmpty(authHeader)) {
                InvocationContext context = ContextUtils.getInvocationContext();
                if (context != null) {
                    Object authObj = context.getContext("Authorization");
                    if (authObj != null) {
                        authHeader = authObj.toString();
                    }
                }
            }

            if (StringUtils.isNoneEmpty(authHeader) && authHeader.startsWith("Basic ")) {
                // 解码BASE64
                String base64Credentials = authHeader.substring("Basic ".length());
                byte[] decodeBytes = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(decodeBytes, StandardCharsets.UTF_8);

                // 分割用户名和密码
                String[] usernamePassword = credentials.split(":", 2);
                if (usernamePassword.length == 2) {
                    String userName = usernamePassword[0];
                    String password = usernamePassword[1];

                    // 用户名和密码的验证
                    if (StringUtils.isEmpty(configUserName) || StringUtils.isEmpty(configPassword)) {
                        logger.error("Username or password configuration is empty");
                        throw new ApiCallException("username or password configuration is empty.");
                    }

                    if (configUserName.equals(userName) && configPassword.equals(password)) {
                        // 认证通过
                        logger.debug("Basic authentication successful");
                        return pjp.proceed();
                    }
                }
            }
        } catch (AuthException e) {
            logger.warn("Authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.warn("Authentication error", e);
            throw new AuthException("Authentication failed", e);
        }

        // 认证失败
        logger.warn("Authentication failed: invalid credentials");
        throw new AuthException("Authentication failed");
    }

    /**
     * Gets the current HTTP request from Spring context.
     *
     * @return the HTTP request
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        throw new AuthException("No HTTP request found in current context");
    }
}
