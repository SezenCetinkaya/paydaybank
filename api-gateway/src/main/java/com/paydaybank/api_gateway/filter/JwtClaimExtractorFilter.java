package com.paydaybank.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Extracts claims for forwarding to downstream services.
 */
@Component
public class JwtClaimExtractorFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtClaimExtractorFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";

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
            // Parse JWT without signature validation
            // Split the token to remove the signature part and parse only header + payload
            String unsignedToken = getUnsignedToken(token);
            
            Claims claims = Jwts.parserBuilder()
                    .build()
                    .parseClaimsJwt(unsignedToken)
                    .getBody();
            
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
            logger.warn("Failed to extract claims from JWT: {}", e.getMessage());
            // On error, pass through without adding headers
            return chain.filter(exchange);
        }
    }
    
    /**
     * Removes the signature part from JWT to allow parsing without validation.
     * JWT structure: header.payload.signature
     * Keeps: header.payload
     */
    private String getUnsignedToken(String token) {
        int lastDotIndex = token.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return token.substring(0, lastDotIndex + 1);
        }
        return token + ".";
    }

    @Override
    public int getOrder() {
        // Execute this filter early in the chain
        return -100;
    }
}
