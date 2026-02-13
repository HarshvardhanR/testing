# 1. Use Java 17 for Spring Boot 3.4 compatibility
FROM eclipse-temurin:17-jre-alpine

# 2. Security: Run as a non-root user (Standard for K8s)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 3. Copy the specific jar name
# Ensure you have <finalName>statistics-service</finalName> in your pom.xml
COPY ./target/statistics-service.jar /app.jar

# 4. Container-aware memory management
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app.jar"]

# 5. Statistics Service usually runs on 7000 in PiggyMetrics
EXPOSE 7000