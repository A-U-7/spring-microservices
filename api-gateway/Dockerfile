# Use lightweight OpenJDK 21 image
FROM openjdk:21-jdk-slim

LABEL maintainer="Spring Microservices Demo" \
      version="1.0" \
      description="API Gateway - Entry Point"

# Set working directory
WORKDIR /app

# Copy the application jar
COPY target/api-gateway-*.jar app.jar

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring \
    && chown -R spring:spring /app
USER spring

# Expose the application port
EXPOSE 8080

# Health check (requires curl)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
