# Stage 1: Build application with Maven
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and POM
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./
RUN chmod +x mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source files and build production JAR
COPY src ./src
RUN ./mvnw package -DskipTests

# Stage 2: Runtime environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy executable JAR from builder stage
COPY --from=builder /app/target/onlineexam-0.0.1-SNAPSHOT.jar app.jar

# Expose server port (Render uses PORT env var)
EXPOSE 8080

# Launch Spring Boot app
# Render injects PORT env var — using sh -c allows shell parameter expansion
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
