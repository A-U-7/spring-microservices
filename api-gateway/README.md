# API Gateway

## Overview

API Gateway is a Spring Cloud Gateway service that acts as a single entry point for all client requests. It provides routing, load balancing, authentication, rate limiting, and circuit breaker functionality for the microservices architecture.

## Features

✅ Request routing to appropriate microservices
✅ Load balancing with Spring Cloud LoadBalancer
✅ Authentication and authorization filters
✅ Rate limiting with Redis
✅ Circuit breaker pattern with Resilience4j
✅ Global logging and monitoring
✅ CORS configuration
✅ Request/response transformation
✅ Health checks and metrics

## Technology Stack

- **Java**: JDK 21
- **Framework**: Spring Boot 3.2.0, Spring Cloud Gateway
- **Load Balancing**: Spring Cloud LoadBalancer
- **Resilience**: Resilience4j (Circuit Breaker)
- **Rate Limiting**: Redis-based implementation
- **Security**: Spring Security (JWT ready)
- **Monitoring**: Micrometer, Prometheus

## Route Configuration

### Service Routes

```yaml
spring:
  cloud:
    gateway:
      routes:
        # User Service
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter:
                  replenishRate: 10
                  burstCapacity: 20
            - name: CircuitBreaker
              args:
                name: user-service
                fallbackUri: forward:/fallback/user-service

        # Order Service
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter:
                  replenishRate: 10
                  burstCapacity: 20
            - name: CircuitBreaker
              args:
                name: order-service
                fallbackUri: forward:/fallback/order-service
```

### Predicate Types

- **Path**: Route based on request path
- **Host**: Route based on host header
- **Method**: Route based on HTTP method
- **Header**: Route based on request headers
- **Query**: Route based on query parameters

### Filter Types

- **AddRequestHeader**: Add header to request
- **AddResponseHeader**: Add header to response
- **RequestRateLimiter**: Rate limiting
- **CircuitBreaker**: Circuit breaker pattern
- **Retry**: Retry failed requests
- **StripPrefix**: Remove path prefix

## Global Filters

### Authentication Filter

```java
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    // Validates JWT tokens for protected endpoints
    // Allows open endpoints without authentication
}
```

### Logging Filter

```java
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {
    // Logs all requests and responses
    // Tracks request duration and status codes
}
```

## Security Configuration

### Open Endpoints (No Auth Required)

```java
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
```

### JWT Token Validation

```java
private boolean isValidToken(String token) {
    // In production, implement proper JWT validation
    // 1. Parse and validate JWT signature
    // 2. Check expiration time
    // 3. Verify claims
    // 4. Check token against blacklist if applicable
    return StringUtils.hasText(token) && token.length() > 10;
}
```

## Resilience Configuration

### Circuit Breaker Settings

```yaml
resilience4j:
  circuitbreaker:
    instances:
      user-service:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
      order-service:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
```

### Time Limiter Settings

```yaml
resilience4j:
  timelimiter:
    instances:
      user-service:
        timeout-duration: 3s
      order-service:
        timeout-duration: 3s
```

## Rate Limiting

### Redis Configuration

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

### Rate Limiter Properties

- **replenishRate**: Number of requests per second
- **burstCapacity**: Maximum number of requests in burst
- **requestedTokens**: Number of tokens consumed per request

## CORS Configuration

### Global CORS Settings

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins:
              - "http://localhost:3000"
              - "http://localhost:4200"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - PATCH
              - OPTIONS
            allowedHeaders:
              - "*"
            allowCredentials: true
```

## Configuration

### Application Properties

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Environment Variables

- `SPRING_PROFILES_ACTIVE`: Active Spring profile (dev, prod, test)
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`: Eureka server URL
- `SERVER_PORT`: Server port (default: 8080)
- `SPRING_REDIS_HOST`: Redis host for rate limiting
- `SPRING_REDIS_PORT`: Redis port for rate limiting

## Running the Service

### Local Development

```bash
# From project root
cd api-gateway
./mvnw spring-boot:run
```

### Docker

```bash
# Build image
docker build -t api-gateway .

# Run container
docker run -p 8080:8080 api-gateway
```

### Docker Compose

```bash
# From project root
docker-compose up api-gateway
```

## Testing the Gateway

### Test Routing

```bash
# Route to User Service
curl http://localhost:8080/api/users

# Route to Order Service
curl http://localhost:8080/api/orders

# Access Swagger UI
curl http://localhost:8080/swagger-ui.html
```

### Test Rate Limiting

```bash
# Send multiple requests quickly
for i in {1..15}; do
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/users
done
# Should see 200 for first 10, then 429 (Too Many Requests)
```

### Test Circuit Breaker

```bash
# Stop User Service
docker-compose stop user-service

# Try to access User Service endpoint
curl http://localhost:8080/api/users
# Should return fallback response

# Start User Service again
docker-compose start user-service
# After recovery period, should work normally
```

## Monitoring

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Gateway Metrics

```bash
curl http://localhost:8080/actuator/prometheus
```

### Key Metrics

- `gateway_requests_seconds_count`: Gateway request count
- `gateway_requests_seconds_sum`: Gateway request duration sum
- `resilience4j_circuitbreaker_calls_total`: Circuit breaker call counts
- `resilience4j_circuitbreaker_state`: Circuit breaker state
- `redis_rate_limiter_tokens_remaining`: Rate limiter tokens remaining

### Routes Endpoint

```bash
curl http://localhost:8080/actuator/gateway/routes
```

## Error Handling

### Custom Error Responses

```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded",
  "path": "/api/users"
}
```

### Fallback Responses

When a service is down, the circuit breaker returns a fallback response:

```json
{
  "error": "Service temporarily unavailable",
  "message": "User Service is currently unavailable. Please try again later.",
  "timestamp": "2024-01-01T00:00:00"
}
```

## Security Considerations

- JWT token validation for protected endpoints
- Rate limiting to prevent abuse
- CORS configuration for cross-origin requests
- Input validation on all endpoints
- Secure headers configuration
- Circuit breaker for DoS protection

## Performance Tuning

### Connection Pool

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          max-connections: 1000
          max-idle-time: 30s
          max-life-time: 60s
```

### Request Timeout

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 1000
        response-timeout: 5s
```

## Development Guidelines

- Follow RESTful conventions
- Implement proper error handling
- Add comprehensive logging
- Configure appropriate timeouts
- Test circuit breaker scenarios
- Monitor rate limiting effectiveness
- Document route changes
- Test fallback mechanisms

## Troubleshooting

### Common Issues

- **Routes not working**: Check service registration in Eureka
- **Rate limiting not working**: Verify Redis connection
- **Circuit breaker not triggering**: Check failure threshold settings
- **CORS issues**: Verify CORS configuration
- **High latency**: Check timeout settings and connection pool

### Debug Mode

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5007"
```

### Enable Debug Logging

```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    org.springframework.cloud.loadbalancer: DEBUG
    org.springframework.cloud.netflix.eureka: DEBUG
```

## Support

For issues specific to API Gateway, check:

- Service logs
- Health endpoint
- Routes endpoint
- Circuit breaker status
- Rate limiting status
- Redis connectivity
- GitHub issues