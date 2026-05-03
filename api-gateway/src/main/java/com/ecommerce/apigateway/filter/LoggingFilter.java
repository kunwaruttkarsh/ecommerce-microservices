package com.ecommerce.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.Instant;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        long startTime = Instant.now().toEpochMilli();
        String path   = exchange.getRequest()
                .getURI().getPath();
        String method = exchange.getRequest()
                .getMethod().toString();

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long duration = Instant.now()
                            .toEpochMilli() - startTime;
                    int status = exchange.getResponse()
                            .getStatusCode().value();
                    log.info("{} {} → {} ({}ms)",
                            method, path, status, duration);
                }));
    }

    @Override
    public int getOrder() {
        return -2;
    }
}