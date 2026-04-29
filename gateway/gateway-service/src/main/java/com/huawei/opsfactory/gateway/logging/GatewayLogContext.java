package com.huawei.opsfactory.gateway.logging;

import java.util.function.Supplier;
import org.apache.logging.log4j.ThreadContext;

public final class GatewayLogContext {

    private GatewayLogContext() {
    }

    public static void run(String requestId, String userId, Runnable action) {
        run(requestId, userId, null, action);
    }

    public static void run(String requestId, String userId, String sessionId, Runnable action) {
        String previousRequestId = ThreadContext.get("requestId");
        String previousUserId = ThreadContext.get("userId");
        String previousSessionId = ThreadContext.get("sessionId");
        try {
            putIfText("requestId", requestId);
            putIfText("userId", userId);
            putIfText("sessionId", sessionId);
            action.run();
        } finally {
            restore("requestId", previousRequestId);
            restore("userId", previousUserId);
            restore("sessionId", previousSessionId);
        }
    }

    public static <T> T call(String requestId, String userId, Supplier<T> action) {
        return call(requestId, userId, null, action);
    }

    public static <T> T call(String requestId, String userId, String sessionId, Supplier<T> action) {
        String previousRequestId = ThreadContext.get("requestId");
        String previousUserId = ThreadContext.get("userId");
        String previousSessionId = ThreadContext.get("sessionId");
        try {
            putIfText("requestId", requestId);
            putIfText("userId", userId);
            putIfText("sessionId", sessionId);
            return action.get();
        } finally {
            restore("requestId", previousRequestId);
            restore("userId", previousUserId);
            restore("sessionId", previousSessionId);
        }
    }

    private static void putIfText(String key, String value) {
        if (value == null || value.isBlank()) {
            ThreadContext.remove(key);
        } else {
            ThreadContext.put(key, value);
        }
    }

    private static void restore(String key, String previousValue) {
        if (previousValue == null || previousValue.isBlank()) {
            ThreadContext.remove(key);
        } else {
            ThreadContext.put(key, previousValue);
        }
    }
}
