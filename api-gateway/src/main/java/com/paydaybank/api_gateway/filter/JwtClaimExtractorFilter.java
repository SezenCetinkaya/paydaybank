package com.paydaybank.api_gateway.filter;

import com.paydaybank.api_gateway.util.JwtValidator;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * GlobalFilter that extracts userId and email claims from JWT tokens
 * and forwards them as HTTP headers (X-User-Id, X-User-Email) to downstream services.
 */
@Component
public class JwtClaimExtractorFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtClaimExtractorFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";

    @Autowired
    private JwtValidator jwtValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        
        // If no Authorization header or doesn't start with "Bearer ", pass through
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }
        
        // Extract token
        String token = authHeader.substring(BEARER_PREFIX.length());
        
        try {
            // Validate token and get claims using JwtValidator
            Claims claims = jwtValidator.validate(token);
            
            // Extract userId and email
            String userId = claims.get("userId", String.class);
            String email = claims.getSubject();
            
            // Create mutated request with new headers
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(USER_ID_HEADER, userId != null ? userId : "")
                    .header(USER_EMAIL_HEADER, email != null ? email : "")
                    .build();
            
            logger.debug("Extracted claims - userId: {}, email: {}", userId, email);
            
            // Continue with mutated request
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
            
        } catch (Exception e) {
            logger.warn("Failed to validate JWT or extract claims: {}", e.getMessage());
            // On error, pass through or we could return 401 Unauthorized here
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        // Execute this filter early in the chain
        return -100;
    }
}
