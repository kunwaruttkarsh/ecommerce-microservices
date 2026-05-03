package com.ecommerce.apigateway.filter;

import com.ecommerce.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private final List<String> PUBLIC_ROUTES = List.of(
            "/api/users/register",
            "/api/users/login",
            "/api/users/validate",
            "/swagger-ui",
            "/api-docs",
            "/aggregate",
            "/webjars"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("Request: {} {}", request.getMethod(), path);

        // Public routes skip karo
        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        // Authorization header check karo
        String authHeader = request.getHeaders()
                .getFirst("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        // Token validate karo
        if (!jwtUtil.validateToken(token)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        // User info extract karo
        String email  = jwtUtil.extractEmail(token);
        String role   = jwtUtil.extractRole(token);
        String userId = jwtUtil.extractUserId(token);

        // Headers add karo for downstream services
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Email", email)
                .header("X-User-Role",  role)
                .header("X-User-Id",    userId)
                .build();

        log.debug("Authenticated: {} role: {}", email, role);

        return chain.filter(
                exchange.mutate()
                        .request(modifiedRequest)
                        .build());
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicRoute(String path) {
        return PUBLIC_ROUTES.stream()
                .anyMatch(path::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange,
                               HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}