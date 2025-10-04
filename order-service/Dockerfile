FROM openjdk:21-jdk-slim

LABEL maintainer="Spring Microservices Demo"
LABEL version="1.0"
LABEL description="Order Service - Order Management"

WORKDIR /app

# Copy the application files
COPY target/order-service-*.jar app.jar

# Create a non-root user
RUN groupadd -r spring && useradd -r -g spring spring
RUN chown -R spring:spring /app
USER spring

# Expose the application port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
