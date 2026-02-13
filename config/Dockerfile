# Use Eclipse Temurin (the industry standard for OpenJDK) with Java 17
FROM eclipse-temurin:17-jre-alpine

# Add a non-root user for security (Best Practice for Kubernetes)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR file
# Note: I used *.jar to be more flexible, but config.jar works if you renamed it
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Optimize for memory (Java 17 is much better at respecting container limits)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app.jar"]

# Healthcheck updated to use the standard Spring Boot 3 path
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s \
  CMD wget --quiet --tries=1 --spider http://localhost:8888/actuator/health || exit 1

EXPOSE 8888