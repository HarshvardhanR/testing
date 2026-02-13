# 1. Use Java 17 (Required for Spring Boot 3+)
FROM eclipse-temurin:17-jre-alpine

# 2. Set a non-root user for K8s security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 3. Copy the JAR using a wildcard (safest way to handle SNAPSHOT names)
COPY ./target/*.jar /app.jar

# 4. Use container-aware memory settings
# Auth services often need a bit more overhead for JWT/Token signing
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app.jar"]

# 5. Auth Service usually runs on 5000 in PiggyMetrics
EXPOSE 5000