# Use a specific tag for the openjdk image to ensure consistency
FROM openjdk:17-slim

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Copy the JAR file and application configuration from the target directory into the container
COPY target/api-0.0.1-SNAPSHOT.jar /api.jar

# Set the entry point for the container to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/api.jar"]
