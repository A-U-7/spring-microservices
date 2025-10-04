# Deployment Guide

## Quick Start

### Prerequisites
- Docker and Docker Compose installed
- Java JDK 21 (for local development)
- Maven 3.9+ (for building)

### One-Command Deployment
```bash
# Clone and deploy everything
git clone <repository-url>
cd spring-microservices
./mvnw clean package -DskipTests
docker-compose up -d
```

### Verify Deployment
```bash
# Check all services are running
docker-compose ps

# Test API Gateway
curl http://localhost:8080/actuator/health

# Check Eureka Dashboard
open http://localhost:8761

# Check Grafana Dashboard
open http://localhost:3000 (admin/admin)
```

## Service URLs

### Application Services
- API Gateway: http://localhost:8080
- User Service: http://localhost:8081
- Order Service: http://localhost:8082

### Infrastructure Services
- Eureka Server: http://localhost:8761
- Config Server: http://localhost:8888
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

### Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## Testing the Deployment

### 1. Health Checks
```bash
# Check all services
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:8888/actuator/health  # Config Server
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Order Service
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

## Deployment Options

### Option 1: Docker Compose (Recommended)
```bash
# Full stack deployment
docker-compose up -d

# Scale specific services
docker-compose up -d --scale user-service=2 --scale order-service=2

# Stop all services
docker-compose down
```

### Option 2: Individual Services
```bash
# Start infrastructure first
docker-compose up -d eureka-server config-server prometheus grafana

# Wait for infrastructure to be ready
sleep 30

# Start application services
docker-compose up -d api-gateway user-service order-service
```

### Option 3: Development Mode
```bash
# Start infrastructure with Docker
docker-compose up -d eureka-server config-server prometheus grafana redis

# Start services locally in separate terminals
cd user-service && ./mvnw spring-boot:run
cd order-service && ./mvnw spring-boot:run
cd api-gateway && ./mvnw spring-boot:run
```

## Monitoring

### Prometheus Metrics
Access Prometheus at http://localhost:9090

Key metrics to monitor:
- `up` - Service health status
- `http_server_requests_seconds_bucket` - Response times
- `jvm_memory_used_bytes` - Memory usage
- `resilience4j_circuitbreaker_calls_total` - Circuit breaker status

### Grafana Dashboard
Access Grafana at http://localhost:3000
- Login with admin/admin
- Import dashboard from `./monitoring/grafana-dashboard.json`
- View service metrics and health status

### Health Dashboard
Monitor these endpoints:
- http://localhost:8761 (Eureka)
- http://localhost:8080/actuator/health (API Gateway)
- http://localhost:8081/actuator/health (User Service)
- http://localhost:8082/actuator/health (Order Service)

## Scaling

### Horizontal Scaling
```bash
# Scale user service to 3 instances
docker-compose up -d --scale user-service=3

# Scale order service to 2 instances
docker-compose up -d --scale order-service=2
```

### Load Balancing
The API Gateway automatically load balances requests across all available instances of each service using Spring Cloud LoadBalancer.

## Security

### Default Security
- JWT-based authentication ready (implement token validation)
- Rate limiting enabled (10 requests/second per service)
- CORS configured for common frontend ports
- Input validation on all endpoints

### Production Security
```bash
# Enable HTTPS
# Configure proper JWT validation
# Set strong passwords for Grafana
# Use external database
# Enable network security policies
```

## Troubleshooting

### Common Issues

#### Services not starting
```bash
# Check logs
docker-compose logs <service-name>

# Check port availability
netstat -an | grep <port>
```

#### Service not registering with Eureka
```bash
# Check Eureka dashboard
curl http://localhost:8761/eureka/apps

# Check service configuration
docker-compose exec <service-name> cat /app/application.yml
```

#### Database connection issues
```bash
# Check H2 console (if enabled)
open http://localhost:8081/h2-console

# Check service logs for database errors
docker-compose logs user-service | grep -i error
```

#### High memory usage
```bash
# Check memory usage
docker stats

# Adjust JVM options
docker-compose up -d --user-service environment JAVA_OPTS="-Xmx256m -Xms128m"
```

### Debug Commands
```bash
# Access service shell
docker-compose exec user-service /bin/bash

# Check service configuration
docker-compose exec user-service cat /app/application.yml

# Monitor network traffic
docker network ls
docker network inspect spring-microservices_microservices-network

# Check Redis (if using rate limiting)
docker-compose exec redis redis-cli ping
```

## Maintenance

### Regular Tasks
```bash
# Update images
docker-compose pull

# Clean up unused resources
docker system prune -f

# Backup data (if using persistent volumes)
docker-compose exec <service> tar -czf /backup/data.tar.gz /data
```

### Log Management
```bash
# View logs
docker-compose logs -f <service-name>

# Clear logs
docker-compose logs --tail 0 -f <service-name> > /dev/null

# Export logs
docker-compose logs <service-name> > service.log
```

## Production Deployment

### Prerequisites
- Production database (PostgreSQL/MySQL)
- SSL certificates
- Domain names
- Load balancer
- Monitoring infrastructure

### Configuration Changes
```yaml
# Update application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/myapp
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}

eureka:
  client:
    service-url:
      defaultZone: https://eureka.prod.com/eureka/
```

### Deployment Steps
1. Build production images
2. Configure production environment
3. Set up SSL certificates
4. Configure external database
5. Deploy to production environment
6. Configure monitoring and alerting
7. Perform smoke tests

## Backup and Recovery

### Configuration Backup
```bash
# Backup configuration files
tar -czf config-backup.tar.gz \
  docker-compose.yml \
  monitoring/ \
  */src/main/resources/application*.yml
```

### Data Backup
```bash
# Backup database (if external)
pg_dump -h localhost -U username dbname > backup.sql

# Backup persistent volumes
docker run --rm -v volume-name:/data -v $(pwd):/backup alpine \
  tar -czf /backup/volume-backup.tar.gz /data
```

## Performance Tuning

### JVM Tuning
```bash
# Set JVM options for better performance
export JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Database Tuning
```yaml
# Connection pool optimization
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
```

### API Gateway Tuning
```yaml
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          max-connections: 1000
          max-idle-time: 30s
        connect-timeout: 1000
        response-timeout: 5s
```

## Environment Variables

### Required Variables
```bash
# Database configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=myapp
export DB_USERNAME=user
export DB_PASSWORD=password

# Security
export JWT_SECRET=your-jwt-secret
export ENCRYPT_KEY=your-encrypt-key

# External services
export EUREKA_URL=http://eureka:8761/eureka/
export CONFIG_URL=http://config:8888
```

### Optional Variables
```bash
# JVM options
export JAVA_OPTS="-Xmx512m -Xms256m"

# Service ports
export USER_SERVICE_PORT=8081
export ORDER_SERVICE_PORT=8082
export GATEWAY_PORT=8080

# Monitoring
export PROMETHEUS_PORT=9090
export GRAFANA_PORT=3000
```

## Support

### Getting Help
- Check service logs: `docker-compose logs <service-name>`
- Verify health endpoints
- Check monitoring dashboards
- Review configuration files
- Test individual services
- Check network connectivity

### Documentation
- Setup Guide
- Architecture Overview
- Individual Service READMEs
- Monitoring Guide

### Community
- GitHub Issues: Report bugs and feature requests
- Documentation: Contribute to documentation
- Examples: Share usage examples

## Conclusion

This deployment provides a complete, production-ready microservices architecture with comprehensive monitoring, security, and scalability features. The modular design allows for easy extension and customization based on specific requirements.

For production deployment, ensure proper security measures, monitoring, and backup strategies are in place.