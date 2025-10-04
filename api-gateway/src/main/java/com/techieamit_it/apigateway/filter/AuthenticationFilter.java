/*
package com.techieamit_it.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    // Define open API endpoints that don't require authentication
    private static final List<String> OPEN_API_ENDPOINTS = List.of(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/refresh",
        "/eureka",
        "/actuator/health",
        "/swagger-ui",
        "/api-docs",
        "/v3/api-docs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        // Check if the endpoint is open (no authentication required)
        if (isOpenEndpoint(path)) {
            logger.debug("Allowing access to open endpoint: {}", path);
            return chain.filter(exchange);
        }
        
        // Check for Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7);
        
        // Here you would typically validate the JWT token
        // For this example, we'll just check if the token exists
        if (!isValidToken(token)) {
            logger.warn("Invalid token for path: {}", path);
            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
        }

        logger.debug("Valid token found for path: {}", path);
        return chain.filter(exchange);
    }

    private boolean isOpenEndpoint(String path) {
        return OPEN_API_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private boolean isValidToken(String token) {
        // In a real implementation, you would:
        // 1. Parse the JWT token
        // 2. Validate the signature
        // 3. Check expiration
        // 4. Verify claims
        // For this example, we'll just check if the token is not empty
        return StringUtils.hasText(token) && token.length() > 10;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        logger.error("Authentication error: {}", err);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}*/
