# syntax=docker/dockerfile:1.4
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copy Gradle configuration files first for better layer caching
COPY gradle/ ./gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./

# Verify Gradle wrapper is executable
RUN chmod +x ./gradlew

# Download dependencies and cache them
RUN ./gradlew dependencies --no-daemon --parallel

# Copy source code after dependency resolution
COPY src/ ./src/

# Build the application, skipping all checks
RUN ./gradlew build -x test -x integrationTest -x jacocoTestReport -x jacocoTestCoverageVerification -x dependencyCheckAnalyze --no-daemon --parallel

# Production image
FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN useradd -r -s /bin/false javauser

WORKDIR /app

# Copy the built jar directly
COPY --from=builder /app/build/libs/*.jar app.jar

# Set proper ownership
RUN chown -R javauser:javauser /app

# Switch to non-root user
USER javauser

# Configure container
EXPOSE 8080 8001 5005
VOLUME /tmp

# Health check for container monitoring
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8001/actuator/health || exit 1

# Use jar directly instead of unpacking
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", \
            "app.jar"]