package com.huawei.opsfactory.gateway.filter;

import com.huawei.opsfactory.gateway.common.constants.GatewayConstants;
import com.huawei.opsfactory.gateway.common.model.UserRole;
import com.huawei.opsfactory.gateway.process.PrewarmService;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(2)
public class UserContextFilter implements WebFilter {

    public static final String USER_ID_ATTR = "userId";
    public static final String USER_ROLE_ATTR = "userRole";

    private final PrewarmService prewarmService;

    public UserContextFilter(PrewarmService prewarmService) {
        this.prewarmService = prewarmService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String userId = request.getHeaders().getFirst(GatewayConstants.HEADER_USER_ID);
        if (userId == null || userId.isBlank()) {
            userId = GatewayConstants.SYS_USER;
        }

        UserRole role = UserRole.fromUserId(userId);

        exchange.getAttributes().put(USER_ID_ATTR, userId);
        exchange.getAttributes().put(USER_ROLE_ATTR, role);

        // Trigger pre-warm for authenticated users
        prewarmService.onUserActivity(userId);

        return chain.filter(exchange);
    }
}
