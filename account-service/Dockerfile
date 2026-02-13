# 1. Use Java 17 (Required for Spring Boot 3+)
FROM eclipse-temurin:17-jre-alpine

# 2. Set a non-root user for K8s security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 3. Copy the JAR from your target folder
COPY ./target/account-service-1.0-SNAPSHOT.jar /app.jar

# 4. Use container-aware memory settings
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app.jar"]

# 5. Account Service usually runs on 6000 in PiggyMetrics
EXPOSE 6000