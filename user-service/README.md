# User Service

## Overview

User Service is a Spring Boot microservice responsible for user management operations including creation, retrieval, updating, and deletion of user accounts.

## Features

✅ **RESTful API with proper HTTP methods**
✅ **Input validation and DTOs**
✅ **Exception handling with custom exceptions**
✅ **Service discovery with Eureka**
✅ **Database integration with JPA/Hibernate**
✅ **Comprehensive logging**
✅ **Metrics collection with Micrometer**
✅ **OpenAPI documentation**
✅ **Health checks and readiness probes**

## Technology Stack

- **Java**: JDK 21
- **Framework**: Spring Boot 3.2.0
- **Database**: H2 (in-memory for development)
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Monitoring**: Micrometer, Prometheus

## API Endpoints

### User Management
```
GET    /api/users                    # Get all users (paginated)
GET    /api/users/list               # Get all users (list)
POST   /api/users                    # Create new user
GET    /api/users/{id}               # Get user by ID
GET    /api/users/username/{username} # Get user by username
PUT    /api/users/{id}               # Update user
DELETE /api/users/{id}               # Delete user
PATCH  /api/users/{id}/deactivate    # Deactivate user
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

### User Entity
```java
{
  "id": 1,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "isActive": true
}
```

## Request/Response Examples

### Create User
**Request:**
```json
POST /api/users
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "isActive": true
}
```

### Update User
**Request:**
```json
PUT /api/users/1
{
  "email": "john.updated@example.com",
  "phoneNumber": "+1987654321"
}
```

## Configuration

### Application Properties
```yaml
server:
  port: 8081

spring:
  application:
    name: user-service
  datasource:
    url: jdbc:h2:mem:userdb
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
```

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (dev, prod, test)
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`: Eureka server URL
- `SERVER_PORT`: Server port (default: 8081)

## Running the Service

### Local Development
```bash
# From project root
cd user-service
./mvnw spring-boot:run
```

### Docker
```bash
# Build image
docker build -t user-service .

# Run container
docker run -p 8081:8081 user-service
```

### Docker Compose
```bash
# From project root
docker-compose up user-service
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
# Create user
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","firstName":"Test","lastName":"User"}'

# Get all users
curl http://localhost:8081/api/users

# Get user by ID
curl http://localhost:8081/api/users/1
```

## Monitoring

### Health Check
```bash
curl http://localhost:8081/actuator/health
```

### Metrics
```bash
curl http://localhost:8081/actuator/prometheus
```

### Key Metrics
- `http_server_requests_seconds_count`: HTTP request count
- `http_server_requests_seconds_sum`: HTTP request duration sum
- `jvm_memory_used_bytes`: JVM memory usage
- `jvm_gc_collection_seconds_sum`: GC collection time

## Error Handling

### Custom Exceptions
- `UserNotFoundException`: When user is not found
- `UserAlreadyExistsException`: When user already exists

### Error Response Format
```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 1",
  "path": "/api/users/1"
}
```

## Security Considerations

- Input validation on all endpoints
- SQL injection prevention via JPA
- Proper error messages (no sensitive data)
- CORS configuration for cross-origin requests

## Performance

- Database connection pooling with HikariCP
- Lazy loading for relationships
- Pagination for large datasets
- Caching support (can be enabled)

## Development Guidelines

- Follow RESTful conventions
- Use DTOs for API contracts
- Implement proper validation
- Add comprehensive logging
- Write unit and integration tests
- Document API changes

## Troubleshooting

### Common Issues
- **Port already in use**: Change port in application.yml
- **Database connection failed**: Check H2 configuration
- **Service not registering**: Check Eureka server status
- **High memory usage**: Adjust JVM heap settings

### Debug Mode
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## Support

For issues specific to User Service, check:
- Service logs
- Health endpoint
- Metrics endpoint
- GitHub issues