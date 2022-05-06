package com.example.kppgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class RoleFilter extends AbstractGatewayFilterFactory<RoleFilter.Config> {

    protected String filterUserRole = null;

    public RoleFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            List<String> userRoles = request.getHeaders().get("userRoles");
            if (userRoles == null) {
                return handleHttpStatus(exchange, HttpStatus.UNAUTHORIZED);
            }

            if (userRoles.get(0).equals(filterUserRole)) {
                return handleHttpStatus(exchange, HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };

    }

    private Mono<Void> handleHttpStatus(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {
    }
}

