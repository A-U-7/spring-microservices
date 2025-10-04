# Config Server

## Overview

Config Server is a Spring Cloud Config Server that provides centralized configuration management for all microservices. It serves configuration from various sources including Git repositories, file system, or memory.

## Features

✅ **Centralized configuration management**
✅ **Environment-specific configurations**
✅ **Configuration encryption and decryption**
✅ **Git backend support**
✅ **File system backend support**
✅ **Native profile support**
✅ **Health checks and monitoring**
✅ **RESTful API for configuration access**

## Technology Stack

- **Java**: JDK 21
- **Framework**: Spring Boot 3.2.0, Spring Cloud Config
- **Configuration Sources**: Git, File System, Native
- **Encryption**: Spring Security Crypto
- **Monitoring**: Micrometer, Prometheus

## Configuration Sources

### Native Configuration (Default)
Configuration files are stored in `src/main/resources/config/`:

```
config/
├── user-service.yml
├── order-service.yml
├── api-gateway.yml
└── application.yml
```

### Git Configuration (Production)
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          clone-on-start: true
          default-label: main
```

### File System Configuration
```yaml
spring:
  cloud:
    config:
      server:
        native:
          search-locations: file:/opt/config
```

## Configuration File Naming

### Standard Format
```
{application}-{profile}.yml
{application}-{profile}.properties
```

### Examples
```
user-service.yml              # Default configuration
user-service-dev.yml          # Development profile
user-service-prod.yml         # Production profile
user-service-test.yml         # Test profile
```

## Configuration Hierarchy

### Precedence Order
1. Application-specific profile configuration
2. Application-specific default configuration
3. Global profile configuration
4. Global default configuration

### Example Resolution
For user-service with dev profile:
- user-service-dev.yml
- user-service.yml
- application-dev.yml
- application.yml

## REST API

### Get Configuration
```bash
# Get configuration for application
GET /{application}/{profile}[/{label}]

# Get configuration with specific label (Git)
GET /user-service/dev/main

# Get configuration file directly
GET /user-service-dev.yml
```

### Examples
```bash
# Get user-service dev configuration
curl http://localhost:8888/user-service/dev

# Get order-service prod configuration
curl http://localhost:8888/order-service/prod

# Get all configuration files
curl http://localhost:8888/user-service/dev
```

### Response Format
```json
{
  "name": "user-service",
  "profiles": ["dev"],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [
    {
      "name": "classpath:/config/user-service-dev.yml",
      "source": {
        "server.port": 8081,
        "spring.application.name": "user-service"
      }
    },
    {
      "name": "classpath:/config/user-service.yml",
      "source": {
        "spring.datasource.url": "jdbc:h2:mem:userdb"
      }
    }
  ]
}
```

## Configuration Encryption

### Enable Encryption
```yaml
spring:
  cloud:
    config:
      server:
        encrypt:
          enabled: true
          key: my-secret-key
```

### Encrypt Configuration
```bash
# Encrypt value
curl -X POST http://localhost:8888/encrypt -d 'my-secret-value'

# Decrypt value
curl -X POST http://localhost:8888/decrypt -d 'encrypted-value'
```

### Encrypted Properties
```yaml
spring:
  datasource:
    password: '{cipher}encrypted-password'
```

## Health Checks

### Health Endpoint
```bash
GET /actuator/health
```

### Health Indicators
- **Config Server Health**: Configuration repository status
- **Disk Space**: Available disk space
- **Custom Health Checks**: Application-specific checks

## Configuration

### Application Properties
```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/config
```

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (native, git)
- `SERVER_PORT`: Server port (default: 8888)
- `SPRING_CLOUD_CONFIG_SERVER_GIT_URI`: Git repository URI
- `SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME`: Git username
- `SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD`: Git password

## Git Backend Configuration

### Public Repository
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          clone-on-start: true
          default-label: main
```

### Private Repository
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          username: your-username
          password: your-token
          clone-on-start: true
          default-label: main
```

### Multiple Repositories
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          repos:
            team-a:
              pattern: team-a-*
              uri: https://github.com/team-a/config-repo
            team-b:
              pattern: team-b-*
              uri: https://github.com/team-b/config-repo
```

## Client Configuration

### Bootstrap Configuration
```yaml
# bootstrap.yml
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
        multiplier: 1.1
        max-interval: 2000
```

### Profile-Specific Configuration
```yaml
spring:
  profiles:
    active: dev
  cloud:
    config:
      profile: dev
      label: main
```

## Running the Service

### Local Development
```bash
# From project root
cd config-server
./mvnw spring-boot:run
```

### Docker
```bash
# Build image
docker build -t config-server .

# Run container
docker run -p 8888:8888 config-server
```

### Docker Compose
```bash
# From project root
docker-compose up config-server
```

## Testing Config Server

### Test Configuration Retrieval
```bash
# Get user-service dev configuration
curl http://localhost:8888/user-service/dev

# Get order-service prod configuration
curl http://localhost:8888/order-service/prod

# Get all configuration files
curl http://localhost:8888/user-service/dev
```

### Test Environment-Specific Config
```bash
# Test with dev profile
curl http://localhost:8888/user-service/dev

# Test with prod profile
curl http://localhost:8888/user-service/prod

# Test with test profile
curl http://localhost:8888/user-service/test
```

### Test Configuration Updates
```bash
# Update configuration file
echo "server.port: 8085" >> config/user-service.yml

# Refresh configuration (if using @RefreshScope)
curl -X POST http://localhost:8081/actuator/refresh
```

## Monitoring

### Health Check
```bash
curl http://localhost:8888/actuator/health
```

### Metrics
```bash
curl http://localhost:8888/actuator/prometheus
```

### Key Metrics
- `config_server_get_requests_total`: Total configuration requests
- `config_server_get_requests_duration_seconds`: Request duration
- `config_server_encryption_requests_total`: Encryption requests
- `config_server_encryption_duration_seconds`: Encryption duration

### Configuration Status
```bash
# Check configuration repository status
curl http://localhost:8888/actuator/health/configServer
```

## Error Handling

### Common Errors
- **Configuration not found**: Check file naming and location
- **Git repository issues**: Verify credentials and connectivity
- **Encryption errors**: Check encryption key configuration
- **Permission errors**: Verify file system permissions

### Error Response Format
```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Configuration not found for application user-service and profile dev",
  "path": "/user-service/dev"
}
```

## Security Considerations

- Enable authentication for production
- Use HTTPS for communication
- Secure configuration repository access
- Encrypt sensitive configuration values
- Implement proper access controls
- Regular security audits

## Production Recommendations

- Use Git backend for version control
- Implement configuration backup strategy
- Set up monitoring and alerting
- Use encryption for sensitive data
- Implement proper access controls
- Configure high availability
- Regular configuration validation

## Troubleshooting

### Common Issues
- **Configuration not found**: Check file naming and location
- **Git connection failed**: Verify credentials and network
- **Encryption not working**: Check key configuration
- **Service not starting**: Verify configuration syntax

### Debug Mode
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5009"
```

### Enable Debug Logging
```yaml
logging:
  level:
    org.springframework.cloud.config: DEBUG
    org.springframework.boot: DEBUG
```

## Development Guidelines

- Use consistent naming conventions
- Document configuration changes
- Version control configuration files
- Test configuration in all environments
- Use encryption for sensitive data
- Implement proper validation

## Support

For issues specific to Config Server, check:
- Service logs
- Health endpoint
- Configuration repository status
- File permissions and locations
- Git connectivity (if applicable)
- GitHub issues