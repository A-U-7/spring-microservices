# Spring Microservices Setup Guide

## Overview

This guide provides comprehensive instructions for setting up and running the Spring Boot microservices architecture with JDK 21.

## Prerequisites

### Required Software
- Java JDK 21 - Download from Oracle or use OpenJDK
- Maven 3.9+ - Download from Apache Maven
- Docker & Docker Compose - Download from Docker
- Git - Download from Git

### Optional Software
- IDE - IntelliJ IDEA, Eclipse, or VS Code
- Postman - For API testing
- HTTP Client - curl, wget, or similar

## Project Structure
```
spring-microservices/
├── eureka-server/           # Service Discovery (Port 8761)
├── config-server/           # Configuration Server (Port 8888)
├── api-gateway/             # API Gateway (Port 8080)
├── user-service/            # User Management Service (Port 8081)
├── order-service/           # Order Management Service (Port 8082)
├── monitoring/              # Prometheus & Grafana configs
├── docker-compose.yml       # Docker Compose configuration
├── README.md               # Project overview
└── SETUP_GUIDE.md          # This file
```

## Quick Start

### Option 1: Docker Compose (Recommended)

#### Clone the repository
```bash
git clone <repository-url>
cd spring-microservices
```

#### Build all services
```bash
./mvnw clean package -DskipTests
```

#### Start all services
```bash
docker-compose up -d
```

#### Verify services are running
```bash
docker-compose ps
```

### Option 2: Manual Setup

#### Start infrastructure services first
```bash
# Terminal 1: Start Eureka Server
cd eureka-server
./mvnw spring-boot:run

# Terminal 2: Start Config Server
cd config-server
./mvnw spring-boot:run

# Terminal 3: Start API Gateway
cd api-gateway
./mvnw spring-boot:run
```

#### Start application services
```bash
# Terminal 4: Start User Service
cd user-service
./mvnw spring-boot:run

# Terminal 5: Start Order Service
cd order-service
./mvnw spring-boot:run
```

## Service URLs

### Application Services
- API Gateway: http://localhost:8080
- User Service: http://localhost:8081
- Order Service: http://localhost:8082

### Infrastructure Services
- Eureka Server: http://localhost:8761
- Config Server: http://localhost:8888

### Monitoring
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)

## API Endpoints

### User Service
```
GET    /api/users                    # Get all users
POST   /api/users                    # Create user
GET    /api/users/{id}               # Get user by ID
GET    /api/users/username/{username} # Get user by username
PUT    /api/users/{id}               # Update user
DELETE /api/users/{id}               # Delete user
PATCH  /api/users/{id}/deactivate    # Deactivate user
```

### Order Service
```
GET    /api/orders                   # Get all orders
POST   /api/orders                   # Create order
GET    /api/orders/{id}              # Get order by ID
GET    /api/orders/order-number/{num} # Get order by number
GET    /api/orders/user/{userId}     # Get orders by user
GET    /api/orders/status/{status}   # Get orders by status
PUT    /api/orders/{id}              # Update order
PATCH  /api/orders/{id}/status       # Update order status
DELETE /api/orders/{id}              # Delete order
```

### Documentation
```
GET    /swagger-ui.html              # Swagger UI
GET    /api-docs                     # OpenAPI docs
```

## Testing the Services

### 1. Check Service Health
```bash
# Check Eureka Server
curl http://localhost:8761/actuator/health

# Check all services via Eureka
curl http://localhost:8761/eureka/apps
```

### 2. Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890"
  }'
```

### 3. Get All Users
```bash
curl http://localhost:8080/api/users
```

### 4. Create an Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productName": "Laptop",
    "quantity": 2,
    "price": 999.99
  }'
```

### 5. Get All Orders
```bash
curl http://localhost:8080/api/orders
```

## Monitoring

### Prometheus Metrics
All services expose metrics at `/actuator/prometheus`

### Grafana Dashboard
- Access Grafana at http://localhost:3000
- Login with admin/admin
- Import the dashboard from `./monitoring/grafana-dashboard.json`

### Key Metrics to Monitor
- Service Health: Up/Down status
- HTTP Request Rate: Requests per second
- Response Time: P50, P95, P99 latencies
- Error Rate: 4xx and 5xx errors
- Memory Usage: JVM heap and non-heap memory
- CPU Usage: System CPU utilization

## Configuration

### Environment Variables
Each service supports the following environment variables:
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (dev, prod, test)
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`: Eureka server URL
- `SPRING_CLOUD_CONFIG_URI`: Config server URL

### Configuration Files
- Application Config: `src/main/resources/application.yml`
- Docker Config: `Dockerfile` in each service directory
- Docker Compose: `docker-compose.yml` in root directory

## Troubleshooting

### Common Issues

#### Services not registering with Eureka
- Check Eureka server is running first
- Verify network connectivity
- Check `application.yml` configuration

#### Database connection issues
- H2 is configured for development (in-memory)
- For production, configure external database

#### Port conflicts
- Ensure no other services are using the same ports
- Modify ports in `application.yml` if needed

#### Docker issues
- Check Docker daemon is running
- Ensure sufficient memory allocated to Docker
- Check logs: `docker-compose logs <service-name>`

### Debugging
```bash
# Check service logs
docker-compose logs -f <service-name>

# Access service shell
docker-compose exec <service-name> /bin/bash

# Monitor network traffic
docker network ls
docker network inspect spring-microservices_microservices-network
```

## Development

### Adding New Services
1. Create new Spring Boot project with JDK 21
2. Add Eureka client dependency
3. Configure `application.yml`
4. Add `Dockerfile`
5. Update `docker-compose.yml`

### Testing
```bash
# Run unit tests
./mvnw test

# Run integration tests
./mvnw verify

# Generate test coverage report
./mvnw jacoco:report
```

### Building for Production
```bash
# Build all services
./mvnw clean package -DskipTests

# Build Docker images
docker-compose build

# Push to registry (if configured)
docker-compose push
```

## Security Considerations

### API Gateway Security
- JWT token validation implemented
- Rate limiting configured
- CORS properly configured

### Service Security
- Input validation on all endpoints
- Proper exception handling
- No sensitive data in logs

### Production Recommendations
- Use external database (PostgreSQL, MySQL)
- Configure SSL/TLS
- Implement proper authentication/authorization
- Use secrets management for sensitive data
- Enable security headers
- Configure firewall rules

## Performance Tuning

### JVM Options
Add to Dockerfiles or docker-compose.yml:
```yaml
environment:
  - JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC
```

### Connection Pooling
Configure HikariCP for database connections:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
```

### Caching
Enable Spring Cache for better performance:
```java
@EnableCaching
@SpringBootApplication
public class Application {
    // ...
}
```

## Support

For issues and questions:
- Check the troubleshooting section
- Review service logs
- Check GitHub issues
- Contact development team