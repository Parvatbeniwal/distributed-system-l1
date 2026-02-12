# Use an official JDK runtime as a base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy Gradle build files & source
COPY build/libs/DistributedSystem-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (optional)
EXPOSE 8080

# Command to run the application (port and node id will be overridden via ENV variables)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
