FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copy Gradle configuration files
COPY gradle/ ./gradle/
COPY gradlew ./gradlew
COPY build.gradle.kts settings.gradle.kts ./

# Verify Gradle wrapper and make it executable
RUN chmod +x ./gradlew

# Create .gradle directory and set permissions
RUN mkdir -p .gradle && \
    chmod -R 777 .gradle

# Download dependencies first
# Using --refresh-dependencies to force download and --stacktrace for better error logging
RUN ./gradlew dependencies --no-daemon --refresh-dependencies --stacktrace || \
    (echo "Retrying dependency download..." && \
     ./gradlew dependencies --no-daemon --refresh-dependencies --stacktrace --info)

# Copy source code after dependency resolution
COPY . .

# Build the application, skipping tests and checks
RUN ./gradlew clean bootJar --no-daemon --parallel --stacktrace

# Production image
FROM openjdk:21-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN useradd -r -s /bin/false javauser

WORKDIR /app

# Copy the built jar
COPY --from=builder /app/build/libs/*.jar app.jar

# Set ownership
RUN chown -R javauser:javauser /app

USER javauser

EXPOSE 9030 8030 5007
VOLUME /tmp

HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8030/actuator/health || exit 1

VOLUME ["/app/logs", "/tmp"]

ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", \
            "app.jar"]
