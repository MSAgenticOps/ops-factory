/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Basic authentication.
 * <p>
 * When applied to a method or type, this annotation triggers the {@link BasicAuthAspect}
 * to validate Basic authentication credentials from the request headers or invocation context.
 *
 * @since 2026-06-06
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface BasicAuth {
}
