# Use Java 17 (Required for Spring Boot 3+)
FROM eclipse-temurin:17-jre-alpine

# Security: Run as a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the specific jar name
# Ensure you have <finalName>notification-service</finalName> in your pom.xml
COPY ./target/notification-service.jar /app.jar

# Better memory handling for Java 17 in containers
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app.jar"]

EXPOSE 8000