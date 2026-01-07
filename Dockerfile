# Build Stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Resolve dependencies first for caching
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src
# Build the application (skipping tests for speed in CI/CD)
RUN ./mvnw clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copy the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Use environment variables for configuration
ENTRYPOINT ["java", "-jar", "app.jar"]
