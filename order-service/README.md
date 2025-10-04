# Order Service

## Overview

Order Service is a Spring Boot microservice responsible for order management operations including creation, retrieval, updating, and tracking of orders. It integrates with User Service to validate user information.

## Features

✅ RESTful API with proper HTTP methods
✅ Integration with User Service via Feign Client
✅ Input validation and DTOs
✅ Exception handling with custom exceptions
✅ Service discovery with Eureka
✅ Database integration with JPA/Hibernate
✅ Comprehensive logging
✅ Metrics collection with Micrometer
✅ OpenAPI documentation
✅ Health checks and readiness probes
✅ Circuit breaker pattern implementation

## Technology Stack

- **Java**: JDK 21
- **Framework**: Spring Boot 3.2.0
- **Database**: H2 (in-memory for development)
- **Service Communication**: OpenFeign
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Monitoring**: Micrometer, Prometheus
- **Resilience**: Circuit breaker pattern

## API Endpoints

### Order Management

```
GET    /api/orders                   # Get all orders (paginated)
GET    /api/orders/list              # Get all orders (list)
POST   /api/orders                   # Create new order
GET    /api/orders/{id}              # Get order by ID
GET    /api/orders/order-number/{num} # Get order by order number
GET    /api/orders/user/{userId}     # Get orders by user ID
GET    /api/orders/user/{userId}/paged # Get orders by user (paginated)
GET    /api/orders/status/{status}   # Get orders by status
GET    /api/orders/status/{status}/paged # Get orders by status (paginated)
PUT    /api/orders/{id}              # Update order
PATCH  /api/orders/{id}/status       # Update order status
DELETE /api/orders/{id}              # Delete order
```

### Documentation

```
GET    /swagger-ui.html              # Swagger UI
GET    /api-docs                     # OpenAPI JSON
GET    /h2-console                   # H2 Database Console
```

### Health & Metrics

```
GET    /actuator/health              # Health check
GET    /actuator/info                # Application info
GET    /actuator/metrics             # Metrics endpoint
GET    /actuator/prometheus          # Prometheus metrics
```

## Data Model

### Order Entity

```java
{
  "id": 1,
  "orderNumber": "ORD-ABC12345",
  "userId": 1,
  "productName": "Laptop",
  "quantity": 2,
  "price": 999.99,
  "totalAmount": 1999.98,
  "status": "PENDING",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Order Status Enum

- **PENDING** - Order created, awaiting confirmation
- **CONFIRMED** - Order confirmed
- **SHIPPED** - Order shipped
- **DELIVERED** - Order delivered
- **CANCELLED** - Order cancelled
- **REFUNDED** - Order refunded

## Request/Response Examples

### Create Order

**Request:**
```json
POST /api/orders
{
  "userId": 1,
  "productName": "Laptop",
  "quantity": 2,
  "price": 999.99
}
```

**Response:**
```json
{
  "id": 1,
  "orderNumber": "ORD-ABC12345",
  "userId": 1,
  "productName": "Laptop",
  "quantity": 2,
  "price": 999.99,
  "totalAmount": 1999.98,
  "status": "PENDING",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Update Order Status

**Request:**
```bash
PATCH /api/orders/1/status?status=CONFIRMED
```

**Response:**
```json
{
  "id": 1,
  "orderNumber": "ORD-ABC12345",
  "userId": 1,
  "productName": "Laptop",
  "quantity": 2,
  "price": 999.99,
  "totalAmount": 1999.98,
  "status": "CONFIRMED",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

## Configuration

### Application Properties

```yaml
server:
  port: 8082

spring:
  application:
    name: order-service
  datasource:
    url: jdbc:h2:mem:orderdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  jpa:
    hibernate:
      ddl-auto: create-drop

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
```

### Environment Variables

- **SPRING_PROFILES_ACTIVE**: Active Spring profile (dev, prod, test)
- **EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE**: Eureka server URL
- **SERVER_PORT**: Server port (default: 8082)

## Service Integration

### User Service Client

Order Service uses OpenFeign to communicate with User Service:

```java
@FeignClient(name = "user-service", fallback = UserServiceFallback.class)
public interface UserServiceClient {
    @GetMapping("/api/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}
```

### Fallback Implementation

```java
@Component
public class UserServiceFallback implements UserServiceClient {
    @Override
    public UserDTO getUserById(Long userId) {
        // Return fallback user when User Service is down
        UserDTO fallbackUser = new UserDTO();
        fallbackUser.setId(userId);
        fallbackUser.setUsername("Unknown User");
        return fallbackUser;
    }
}
```

## Running the Service

### Local Development

```bash
# From project root
cd order-service
./mvnw spring-boot:run
```

### Docker

```bash
# Build image
docker build -t order-service .

# Run container
docker run -p 8082:8082 order-service
```

### Docker Compose

```bash
# From project root
docker-compose up order-service
```

## Testing

### Unit Tests

```bash
./mvnw test
```

### Integration Tests

```bash
./mvnw verify
```

### Manual Testing

```bash
# Create order (assuming user exists)
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productName":"Laptop","quantity":2,"price":999.99}'

# Get all orders
curl http://localhost:8082/api/orders

# Get orders by user
curl http://localhost:8082/api/orders/user/1

# Update order status
curl -X PATCH "http://localhost:8082/api/orders/1/status?status=CONFIRMED"
```

## Monitoring

### Health Check

```bash
curl http://localhost:8082/actuator/health
```

### Metrics

```bash
curl http://localhost:8082/actuator/prometheus
```

### Key Metrics

- **http_server_requests_seconds_count**: HTTP request count
- **http_server_requests_seconds_sum**: HTTP request duration sum
- **jvm_memory_used_bytes**: JVM memory usage
- **feign_client_duration_seconds**: Feign client call duration
- **circuitbreaker_calls_total**: Circuit breaker call counts

## Error Handling

### Custom Exceptions

- **OrderNotFoundException**: When order is not found
- **UserNotFoundException**: When user validation fails

### Error Response Format

```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: 1",
  "path": "/api/orders/1"
}
```

## Security Considerations

- Input validation on all endpoints
- SQL injection prevention via JPA
- Proper error messages (no sensitive data)
- CORS configuration for cross-origin requests
- Feign client timeout and retry configuration

## Performance

- Database connection pooling with HikariCP
- Lazy loading for relationships
- Pagination for large datasets
- Caching support (can be enabled)
- Circuit breaker for resilience
- Connection timeout configuration

## Development Guidelines

- Follow RESTful conventions
- Use DTOs for API contracts
- Implement proper validation
- Add comprehensive logging
- Write unit and integration tests
- Document API changes
- Handle service failures gracefully
- Implement proper fallback mechanisms

## Troubleshooting

### Common Issues

- **Port already in use**: Change port in application.yml
- **Database connection failed**: Check H2 configuration
- **Service not registering**: Check Eureka server status
- **User Service unavailable**: Check circuit breaker status
- **High memory usage**: Adjust JVM heap settings

### Debug Mode

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5006"
```

### Check Circuit Breaker Status

```bash
curl http://localhost:8082/actuator/health
# Look for circuitBreaker section in response
```

## Support

For issues specific to Order Service, check:
- Service logs
- Health endpoint
- Metrics endpoint
- Circuit breaker status
- User Service connectivity
- GitHub issues
