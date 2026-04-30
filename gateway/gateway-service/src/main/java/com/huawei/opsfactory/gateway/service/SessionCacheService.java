package com.huawei.opsfactory.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Short-lived cache for aggregated session lists, keyed by userId.
 * Reduces repeated full-fetches from all goosed instances during pagination.
 */
@Service
public class SessionCacheService {

    private static final Logger log = LoggerFactory.getLogger(SessionCacheService.class);
    private static final long DEFAULT_TTL_MS = 30_000;

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public List<Map<String, Object>> get(String userId) {
        CacheEntry entry = cache.get(userId);
        if (entry == null || System.currentTimeMillis() - entry.timestamp > DEFAULT_TTL_MS) {
            return null;
        }
        return entry.sessions;
    }

    public void put(String userId, List<Map<String, Object>> sessions) {
        cache.put(userId, new CacheEntry(sessions, System.currentTimeMillis()));
        log.debug("[SESSION-CACHE] cached userId={} count={}", userId, sessions.size());
    }

    public void invalidate(String userId) {
        cache.remove(userId);
        log.debug("[SESSION-CACHE] invalidated userId={}", userId);
    }

    private record CacheEntry(List<Map<String, Object>> sessions, long timestamp) {}
}
