# Use OpenJDK 21 slim image as base
FROM openjdk:21-jdk-slim

# Add maintainer and version labels
LABEL maintainer="Spring Microservices Demo"
LABEL version="1.0"
LABEL description="User Service - User Management"

# Set working directory
WORKDIR /app

# Copy the application JAR file
COPY target/user-service-*.jar app.jar

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
RUN chown -R spring:spring /app
USER spring

# Expose the application port
EXPOSE 8081

# Health check configuration
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]