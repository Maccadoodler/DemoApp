# Use Eclipse Temurin Java 24 (official JDK 24 image)
FROM eclipse-temurin:24-jdk AS runtime

WORKDIR /app

# Copy your JAR file from local build
COPY target/task-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8443
EXPOSE 8085

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]