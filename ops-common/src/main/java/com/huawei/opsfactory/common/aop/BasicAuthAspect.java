/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.aop;

import com.huawei.opsfactory.common.config.CommonProperties;
import com.huawei.opsfactory.common.exception.AuthException;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.swagger.invocation.context.ContextUtils;
import org.apache.servicecomb.swagger.invocation.context.InvocationContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class BasicAuthAspect {
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthAspect.class);

    private final HttpServletRequest request;

    private final CommonProperties commonProperties;

    /**
     * Constructs a new BasicAuthAspect with the given request and common properties.
     *
     * @param request the HTTP servlet request, may be null in non-servlet environments
     * @param commonProperties the common properties containing authentication credentials
     */
    public BasicAuthAspect(@Autowired(required = false) HttpServletRequest request,
                           @Autowired CommonProperties commonProperties) {
        this.request = request;
        this.commonProperties = commonProperties;
    }

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
    @Around("@annotation(com.huawei.opsfactory.common.aop.BasicAuth)")
    public Object basicAuth(ProceedingJoinPoint pjp) throws Throwable {
        logger.debug("BasicAuth triggered for: {}", pjp.getSignature().getName());
        String authHeader = null;

        try {
            // 从请求头中获取Authorization（可能在非 servlet 环境抛异常）
            if (request != null) {
                authHeader = request.getHeader("Authorization");
            }

            // 从 ServiceComb 上下文获取
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
                    if (StringUtils.isEmpty(commonProperties.getUserName()) ||
                        StringUtils.isEmpty(commonProperties.getPassword())) {
                        logger.warn("Machine authentication not configured, rejecting @BasicAuth request");
                        throw new AuthException("Machine authentication not configured");
                    }

                    if (commonProperties.getUserName().equals(userName) &&
                        commonProperties.getPassword().equals(password)) {
                        // 认证通过
                        logger.debug("Basic authentication successful");
                        return pjp.proceed();
                    }
                }
            }
        } catch (AuthException e) {
            logger.warn("Authentication failed: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            // Base64 解码失败
            logger.warn("Authentication error: invalid credentials format - {}", e.getMessage());
            throw new AuthException("Authentication failed", e);
        }

        // 认证失败
        logger.warn("Authentication failed: invalid credentials");
        throw new AuthException("Authentication failed");
    }
}
