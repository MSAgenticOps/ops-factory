package com.huawei.opsfactory.gateway.qos.dv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DvAuthService {
    private static final Logger log = LoggerFactory.getLogger(DvAuthService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final long TOKEN_TTL_MS = 30 * 60 * 1000L; // 30 minutes

    private final DvSslContextFactory sslFactory;
    private final ConcurrentHashMap<String, TokenInfo> tokenCache = new ConcurrentHashMap<>();

    public DvAuthService(DvSslContextFactory sslFactory) {
        this.sslFactory = sslFactory;
    }

    public synchronized TokenInfo getSSOToken(DvEnvironmentInfo env) {
        String cacheKey = env.getServerUrl() + ":" + env.getUtmUser();
        TokenInfo cached = tokenCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached;
        }
        TokenInfo newToken = fetchNewToken(env);
        tokenCache.put(cacheKey, newToken);
        return newToken;
    }

    public Map<String, String> buildAuthHeaders(DvEnvironmentInfo env) {
        String cacheKey = env.getServerUrl() + ":" + env.getUtmUser();
        TokenInfo info = tokenCache.get(cacheKey);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("X-Auth-Token", getSSOToken(env).token);
        headers.put("roaRand", getSSOToken(env).roaRand);
        return headers;
    }

    public void clearCache() {
        tokenCache.clear();
    }

    private TokenInfo fetchNewToken(DvEnvironmentInfo env) {
        try {
            SslContext sslContext = sslFactory.createSslContext(env.getCrtContent(), env.getCrtFileName(), env.isStrictSsl());
            HttpClient httpClient = HttpClient.create()
                    .secure(t -> t.sslContext(sslContext)
                            .handshakeTimeout(Duration.ofSeconds(10)))
                    .responseTimeout(Duration.ofSeconds(30));

            WebClient webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(env.getServerUrl())
                    .build();

            String loginBody = MAPPER.writeValueAsString(Map.of(
                    "grantType", "password",
                    "userName", env.getUtmUser(),
                    "value", env.getUtmPassword()));

            String response = webClient.put()
                    .uri("/rest/plat/smapp/v1/sessions")
                    .header("Content-Type", "application/json")
                    .body(Mono.just(loginBody), String.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(30));

            JsonNode json = MAPPER.readTree(response);
            String token = json.has("accessSession") ? json.get("accessSession").asText() : null;
            if (token == null || token.isBlank()) {
                throw new RuntimeException("No token in login response from " + env.getServerUrl());
            }

            String roaRand = json.has("roaRand") ? json.get("roaRand").asText() : null;
            if (roaRand == null || roaRand.isBlank()) {
                throw new RuntimeException("No roaRand in login response from " + env.getServerUrl());
            }

            long ttlMs = TOKEN_TTL_MS;
            if (json.has("expires") && !json.get("expires").isNull()) {
                try {
                    ttlMs = json.get("expires").asLong() * 1000L;
                } catch (NumberFormatException e) {
                    log.warn("Invalid expires value in login response, using default TTL");
                }
            }
            log.info("DV SSO token acquired for {}", env.getEnvCode());
            return new TokenInfo(token, roaRand, System.currentTimeMillis(), ttlMs);
        } catch (Exception e) {
            log.error("Failed to get SSO token from {}: {}", env.getServerUrl(), e.getMessage());
            throw new RuntimeException("DV SSO login failed", e);
        }
    }

    private static class TokenInfo {
        final String token;
        final String roaRand;
        final long createdAt;
        final long ttlMs;
        TokenInfo(String token, String roaRand, long createdAt, long ttlMs) {
            this.token = token;
            this.roaRand = roaRand;
            this.createdAt = createdAt;
            this.ttlMs = ttlMs;
        }
        boolean isExpired() {
            return System.currentTimeMillis() - createdAt > ttlMs;
        }
    }
}
