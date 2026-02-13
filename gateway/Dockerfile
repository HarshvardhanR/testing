# Use Java 17 for Spring Boot 3.4 compatibility
FROM eclipse-temurin:17-jre-alpine

# Security: Run as a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the specific jar name you requested
# Ensure you have <finalName>gateway</finalName> in your pom.xml
COPY ./target/gateway.jar /app.jar

# Container-aware memory settings (better than fixed -Xmx200m)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app.jar"]

EXPOSE 4000