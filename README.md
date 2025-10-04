# Spring Boot Microservices Architecture

## Overview

This project demonstrates a modern microservices architecture using Spring Boot 3.x with JDK 21, incorporating Spring Cloud features and comprehensive observability.

## Architecture Components

### Core Services
- **User Service (Service A)** - Manages user information and authentication
- **Order Service (Service B)** - Handles order processing and management

### Infrastructure Services
- **API Gateway** - Spring Cloud Gateway for routing and load balancing
- **Service Discovery** - Eureka Server for service registration and discovery
- **Config Server** - Distributed configuration management
- **Monitoring Stack** - Prometheus + Grafana for observability

## Technology Stack

- **Java**: JDK 21
- **Framework**: Spring Boot 3.x, Spring Cloud 2023.x
- **Database**: H2 (in-memory) for development
- **Message Queue**: Optional RabbitMQ for async communication
- **Monitoring**: Micrometer, Prometheus, Grafana
- **Logging**: Logback with structured logging
- **Documentation**: OpenAPI 3.0 (Swagger)

## Project Structure
```
spring-microservices/
├── user-service/           # User Management Service
├── order-service/          # Order Processing Service
├── api-gateway/           # Spring Cloud Gateway
├── eureka-server/         # Service Discovery
├── config-server/         # Configuration Server
├── monitoring/            # Prometheus & Grafana configs
├── docker-compose.yml     # Local development setup
└── README.md             # This file
```

## Features Implemented

1. **Proper layered architecture** (Controller → Service → Repository -> Model)
2. **Exception handling** with custom exceptions
3. **Input validation** and DTOs
4. **Service discovery** with Eureka
5. **API Gateway** with routing
6. **Distributed configuration**
7. **Comprehensive logging**
8. **Metrics collection** with Micrometer
9. **Distributed tracing**
10 **Health checks** and readiness probes
11. **Docker containerization**
12. **OpenAPI documentation**

## Getting Started

See individual service README files for detailed setup instructions.

### Quick Start
```bash
# Start infrastructure services
docker-compose up -d eureka-server config-server prometheus grafana

# Start microservices
./mvnw spring-boot:run -pl user-service
./mvnw spring-boot:run -pl order-service
./mvnw spring-boot:run -pl api-gateway
```
