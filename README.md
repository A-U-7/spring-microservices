# Spring Boot Microservices Architecture

<div align="center">

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)]()
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.3-6DB33F?style=for-the-badge&logo=spring&logoColor=white)]()
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)]()

**A Modern Microservices Architecture with Spring Cloud Ecosystem**

</div>

---

## Overview

This project demonstrates a **production-ready microservices architecture** using **Spring Boot 3.x** with **JDK 21**, incorporating the complete **Spring Cloud ecosystem** for building scalable, resilient, and observable distributed systems.
**Key Features:**
- Service Discovery with Netflix Eureka
- API Gateway with Spring Cloud Gateway
- Centralized Configuration with Spring Cloud Config
- Comprehensive Monitoring with Prometheus & Grafana
- Inter-service Communication with OpenFeign
- API Documentation with OpenAPI 3.0
- Container Ready with Docker support

---

## Architecture

```mermaid
graph TB
    A[API Gateway<br/>Port: 8080] --> B[Eureka Server<br/>Port: 8761]
    A --> C[Config Server<br/>Port: 8888]
    A --> D[User Service<br/>Port: 8081]
    A --> E[Order Service<br/>Port: 8082]

    B --> D
    B --> E
    C --> D
    C --> E

    D --> F[(H2 Database<br/>User DB)]
    E --> G[(H2 Database<br/>Order DB)]

    H[Prometheus<br/>Port: 9090] --> D
    H --> E
    H --> A

### 🧩 **Module Breakdown**

| Module | Port | Description |
|--------|------|-------------|
| **api-gateway** | 8080 | Spring Cloud Gateway - Routing & Load Balancing |
| **user-service** | 8081 | User Management Microservice |
| **order-service** | 8082 | Order Processing Microservice |
| **eureka-server** | 8761 | Service Discovery Server |
| **config-server** | 8888 | Centralized Configuration Management |
| **monitoring** | - | Prometheus, Grafana & Zipkin Configuration |

---

## 🔧 Technology Stack

<div align="center">

### **Core Technologies**
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming Language |
| **Spring Boot** | 3.2.4 | Application Framework |
| **Spring Cloud** | 2023.0.3 | Microservices Framework |
| **Maven** | 3.9+ | Build Tool |

### **Infrastructure & Cloud**
| Component | Technology | Purpose |
|-----------|------------|---------|
| **Service Discovery** | Netflix Eureka | Service Registration & Discovery |
| **API Gateway** | Spring Cloud Gateway | Request Routing & Filtering |
| **Configuration** | Spring Cloud Config | Centralized Configuration |
| **Load Balancing** | Spring Cloud LoadBalancer | Client-side Load Balancing |

### **Data & Communication**
| Component | Technology | Purpose |
|-----------|------------|---------|
| **Database** | H2 Database | In-memory Database (Dev) |
| **Inter-service** | OpenFeign | Declarative HTTP Client |
| **Validation** | Jakarta Validation | Input Validation |

### **Observability & Monitoring**
| Component | Technology | Purpose |
|-----------|------------|---------|
| **Metrics** | Micrometer + Prometheus | Application Metrics |
| **Visualization** | Grafana | Dashboards & Analytics |
| **Tracing** | Zipkin | Distributed Tracing |
| **Logging** | Logback | Structured Logging |

### **Development & DevOps**
| Component | Technology | Purpose |
|-----------|------------|---------|
| **Documentation** | OpenAPI 3.0 | API Documentation |
| **Container** | Docker | Containerization |
| **Health Checks** | Spring Boot Actuator | Application Health |

-

 

## 🚀 Quick Start

### **Prerequisites**
- **Java 21** or higher
- **Maven 3.9** or higher
- **Docker & Docker Compose** (optional)

### **1. Clone the Repository**
```bash
git clone <repository-url>
cd spring-microservices
```

### **2. Start Infrastructure Services**
```bash
# Using Maven (Individual Services)
./mvnw spring-boot:run -pl eureka-server
./mvnw spring-boot:run -pl config-server

# Or using Docker Compose (Recommended)
docker-compose up -d eureka-server config-server prometheus grafana zipkin
```

### **3. Start Microservices**
```bash
# Terminal 1 - User Service
./mvnw spring-boot:run -pl user-service

# Terminal 2 - Order Service
./mvnw spring-boot:run -pl order-service

# Terminal 3 - API Gateway
./mvnw spring-boot:run -pl api-gateway
```

### **4. Verify Services**
```bash
# Check Eureka Dashboard
curl http://localhost:8761

# Check Config Server
curl http://localhost:8888/api-gateway/default

# Check API Gateway Health
curl http://localhost:8080/actuator/health
```

---

## 🔗 Service Endpoints

### **🌐 Infrastructure Services**

| Service | URL | Description |
|---------|-----|-------------|
| **🔍 Eureka Server** | http://localhost:8761 | Service Discovery Dashboard |
| **⚙️ Config Server** | http://localhost:8888 | Centralized Configuration |
| **🌐 API Gateway** | http://localhost:8080 | Main Gateway Endpoint |

### **🏢 Application Services**

| Service | URL | Description |
|---------|-----|-------------|
| **👥 User Service** | http://localhost:8081 | User Management API |
| **🛒 Order Service** | http://localhost:8082 | Order Processing API |

### **🗄️ Database Consoles**

| Database | URL | Description |
|----------|-----|-------------|
| **👥 User Service H2** | http://localhost:8081/h2-console | In-memory User Database |
| **🛒 Order Service H2** | http://localhost:8082/h2-console | In-memory Order Database |

---

## 📊 Monitoring & Observability

### **📈 Metrics & Dashboards**

| Tool | URL | Description |
|------|-----|-------------|
| **📊 Prometheus** | http://localhost:9090 | Metrics Collection & Querying |
| **📊 Grafana** | http://localhost:3000 | Dashboards & Visualization |
| **🔍 Zipkin** | http://localhost:9411 | Distributed Tracing |

### **🔍 Health Endpoints**

| Service | Health URL | Metrics URL |
|---------|------------|-------------|
| **🌐 API Gateway** | http://localhost:8080/actuator/health | http://localhost:8080/actuator/prometheus |
| **👥 User Service** | http://localhost:8081/actuator/health | http://localhost:8081/actuator/prometheus |
| **🛒 Order Service** | http://localhost:8082/actuator/health | http://localhost:8082/actuator/prometheus |

### **📋 Common Health Check Commands**

```bash
# Check all services health
curl http://localhost:8080/actuator/health    # API Gateway
curl http://localhost:8081/actuator/health    # User Service
curl http://localhost:8082/actuator/health    # Order Service

# Check service metrics
curl http://localhost:8080/actuator/prometheus | head -20
```

---

## 🐳 Docker Setup

### **Complete Stack (Recommended)**

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### **Service-specific Commands**

```bash
# Start only infrastructure
docker-compose up -d eureka-server config-server

# Start only monitoring stack
docker-compose up -d prometheus grafana zipkin

# Scale a service
docker-compose up -d --scale user-service=2
```

---

## 🔍 API Documentation

### **📚 Interactive API Documentation**

| Service | Swagger UI | OpenAPI Spec |
|---------|------------|--------------|
| **🌐 API Gateway** | http://localhost:8080/swagger-ui.html | http://localhost:8080/api-docs |
| **👥 User Service** | http://localhost:8081/swagger-ui.html | http://localhost:8081/api-docs |
| **🛒 Order Service** | http://localhost:8082/swagger-ui.html | http://localhost:8082/api-docs |

### **🚀 Sample API Calls**

#### **User Service**
```bash
# Create a user
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Get user by ID
curl http://localhost:8081/api/users/1
```

#### **Order Service**
```bash
# Create an order
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productName": "Laptop",
    "quantity": 1,
    "price": 999.99
  }'

# Get all orders
curl http://localhost:8082/api/orders
```

---

## 📚 Features

### **✅ Implemented Features**

#### **🏗️ Architecture & Design**
- [x] **Layered Architecture** (Controller → Service → Repository)
- [x] **Clean Code Principles** with proper separation of concerns
- [x] **Exception Handling** with custom exceptions and global handler
- [x] **Input Validation** using Jakarta Validation
- [x] **DTO Pattern** for data transfer objects

#### **☁️ Microservices Patterns**
- [x] **Service Discovery** with Netflix Eureka
- [x] **API Gateway** with Spring Cloud Gateway
- [x] **Centralized Configuration** with Spring Cloud Config
- [x] **Load Balancing** with Spring Cloud LoadBalancer
- [x] **Circuit Breaker** pattern (Configured)
- [x] **Inter-service Communication** with OpenFeign

#### **📊 Observability**
- [x] **Application Metrics** with Micrometer
- [x] **Health Checks** with Spring Boot Actuator
- [x] **Distributed Tracing** with Zipkin
- [x] **Structured Logging** with Logback
- [x] **Prometheus Integration** for metrics collection

#### **🔧 Development Experience**
- [x] **OpenAPI Documentation** with Swagger UI
- [x] **H2 Database Console** for development
- [x] **Hot Reloading** with Spring Boot DevTools
- [x] **Comprehensive Testing** structure
- [x] **Docker Containerization** ready

### **🚧 Planned Features**
- [ ] **API Rate Limiting** with Redis
- [ ] **Message Queue** integration with RabbitMQ
- [ ] **Database Migration** with Flyway/Liquibase
- [ ] **JWT Authentication** and Authorization
- [ ] **API Versioning** strategy
- [ ] **Database Sharding** for scalability

---

## 🎨 Contributing

### **Development Workflow**

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### **Code Style Guidelines**
- Follow **Java Naming Conventions**
- Write **descriptive commit messages**
- Add **unit tests** for new features
- Update **documentation** as needed

---

<div align="center">

Built by Amit Upadhyay [[A-U-7](https://github.com/A-U-7)]

*Happy Microservicing! 🚀* 

 

</div>