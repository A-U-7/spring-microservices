# Eureka Server

## Overview

Eureka Server is a Spring Boot application that provides service discovery functionality for the microservices architecture. It acts as a central registry where all microservices can register themselves and discover other services.

## Features

✅ **Service registration and discovery**
✅ **Health check monitoring**
✅ **Load balancer integration**
✅ **Service metadata management**
✅ **Dashboard for service monitoring**
✅ **High availability support**
✅ **RESTful API for service management**

## Technology Stack

- **Java**: JDK 21
- **Framework**: Spring Boot 3.2.0, Spring Cloud Netflix Eureka
- **Monitoring**: Micrometer, Prometheus
- **Dashboard**: Built-in Eureka dashboard

## Service Registration

### Registration Process
1. Microservices start up with Eureka client configuration
2. Services register themselves with Eureka Server
3. Eureka Server maintains a registry of all services
4. Services can discover other services through Eureka
5. Eureka sends heartbeat to check service health

### Registration Configuration
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    healthcheck:
      enabled: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
```

## Eureka Dashboard

### Access Dashboard
**URL**: http://localhost:8761

### Features:
- View all registered services
- Check service status and metadata
- Monitor service health
- View instance details

### Dashboard Information
- **Application**: Service name
- **Status**: UP, DOWN, STARTING, OUT_OF_SERVICE
- **Instance ID**: Unique identifier
- **Port**: Service port
- **IP Address**: Service IP address
- **Metadata**: Additional service information

## REST API

### Get All Applications
```bash
GET /eureka/apps
Accept: application/json
```

### Get Application Details
```bash
GET /eureka/apps/{appName}
Accept: application/json
```

### Get Instance Details
```bash
GET /eureka/apps/{appName}/{instanceId}
Accept: application/json
```

### Health Check
```bash
GET /actuator/health
```

## Configuration

### Application Properties
```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 60000
```

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (dev, prod, test)
- `SERVER_PORT`: Server port (default: 8761)
- `EUREKA_INSTANCE_HOSTNAME`: Eureka instance hostname

## Service Metadata

### Standard Metadata
- `management.port`: Management endpoint port
- `management.context-path`: Management endpoint path
- `jmx.port`: JMX port

### Custom Metadata
Services can add custom metadata:
```yaml
eureka:
  instance:
    metadata-map:
      version: 1.0.0
      environment: production
      description: User Management Service
```

## Health Checks

### Service Health
Eureka uses health checks to determine service status:
- **UP**: Service is healthy and available
- **DOWN**: Service is unhealthy
- **STARTING**: Service is starting up
- **OUT_OF_SERVICE**: Service is temporarily unavailable

### Health Check Configuration
```yaml
eureka:
  client:
    healthcheck:
      enabled: true
```

## High Availability

### Multiple Eureka Instances
For production, run multiple Eureka instances:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8762/eureka/
```

### Peer Awareness
Eureka instances can sync with each other:
```yaml
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://peer1:8761/eureka/,http://peer2:8762/eureka/
```

## Running the Service

### Local Development
```bash
# From project root
cd eureka-server
./mvnw spring-boot:run
```

### Docker
```bash
# Build image
docker build -t eureka-server .

# Run container
docker run -p 8761:8761 eureka-server
```

### Docker Compose
```bash
# From project root
docker-compose up eureka-server
```

## Testing Eureka

### Check Service Registration
```bash
# View all registered services
curl http://localhost:8761/eureka/apps

# Check specific service
curl http://localhost:8761/eureka/apps/USER-SERVICE

# Check health
curl http://localhost:8761/actuator/health
```

### Manual Service Registration (for testing)
```bash
curl -X POST http://localhost:8761/eureka/apps/TEST-SERVICE \
  -H "Content-Type: application/json" \
  -d '{
    "instance": {
      "instanceId": "test-service:8080",
      "hostName": "localhost",
      "app": "TEST-SERVICE",
      "ipAddr": "127.0.0.1",
      "status": "UP",
      "port": {"$": 8080, "@enabled": true},
      "vipAddress": "test-service",
      "dataCenterInfo": {
        "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
        "name": "MyOwn"
      }
    }
  }'
```

## Monitoring

### Health Check
```bash
curl http://localhost:8761/actuator/health
```

### Metrics
```bash
curl http://localhost:8761/actuator/prometheus
```

### Key Metrics
- `eureka_server_registry_size`: Number of registered instances
- `eureka_server_renewal_threshold`: Renewal threshold
- `eureka_server_renewals_last_min`: Renewals in last minute
- `eureka_server_evictions_total`: Total evictions
- `eureka_server_self_preservation_enabled`: Self-preservation status

### Dashboard Metrics
- **Registered Services**: Total number of services
- **Available Instances**: Total available service instances
- **Unavailable Instances**: Total unavailable service instances
- **Self Preservation Mode**: Whether self-preservation is active

## Configuration Options

### Server Configuration
```yaml
eureka:
  server:
    # Enable self-preservation mode
    enable-self-preservation: true

    # Eviction interval (ms)
    eviction-interval-timer-in-ms: 60000

    # Renewal threshold update interval (ms)
    renewal-threshold-update-interval-ms: 900000

    # Peer eureka nodes update interval (ms)
    peer-eureka-nodes-update-interval-ms: 600000
```

### Client Configuration
```yaml
eureka:
  instance:
    # Lease renewal interval (seconds)
    lease-renewal-interval-in-seconds: 30

    # Lease expiration duration (seconds)
    lease-expiration-duration-in-seconds: 90

    # Instance metadata
    metadata-map:
      version: 1.0.0

  client:
    # Registry fetch interval (seconds)
    registry-fetch-interval-seconds: 30

    # Instance info replication interval (seconds)
    instance-info-replication-interval-seconds: 30
```

## Troubleshooting

### Common Issues
- **Services not registering**: Check network connectivity and configuration
- **Services showing as DOWN**: Check health check endpoints
- **Self-preservation mode**: Normal when network issues occur
- **Memory issues**: Adjust heap settings for large registries

### Debug Mode
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5008"
```

### Enable Debug Logging
```yaml
logging:
  level:
    com.netflix.eureka: DEBUG
    com.netflix.discovery: DEBUG
```

## Performance Tuning

### Memory Settings
```yaml
# Set appropriate heap size
JAVA_OPTS=-Xmx512m -Xms256m
```

### Registry Settings
```yaml
eureka:
  server:
    # Max threads for handling requests
    max-threads-for-peer-replication: 20

    # Max elements in peer replication queue
    max-elements-in-peer-replication-pool: 10000
```

## Security Considerations

- Enable authentication for Eureka dashboard in production
- Use HTTPS for service communication
- Implement network security policies
- Monitor for unauthorized access
- Regular security updates

## Production Recommendations

- Run multiple Eureka instances for HA
- Configure proper JVM heap settings
- Monitor registry size and performance
- Set up alerts for service failures
- Use persistent storage for registry backup
- Implement proper backup and recovery procedures

## Support

For issues specific to Eureka Server, check:
- Service logs
- Health endpoint
- Registry size and performance
- Network connectivity
- Service configuration
- GitHub issues