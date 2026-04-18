# ==========================================
# STAGE 1: BUILD (The Heavy Lifter)
# ==========================================
# We use a full Maven image to compile the code.
# This contains the JDK and all build tools.
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies first (this caches them for faster future builds!)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code
COPY src ./src

# Build the application, skipping tests for the image creation phase
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: PRODUCTION RUNTIME (The Secure Vault)
# ==========================================
# We use a tiny JRE (Java Runtime Environment) image.
# It cannot compile code, which severely limits the attack surface.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# SECURITY: Create a non-root user and group
# Running as root inside Docker is a massive security vulnerability.
RUN addgroup -S spring && adduser -S spring -G spring

# Grab ONLY the compiled .jar file from Stage 1
COPY --from=builder /app/target/*.jar app.jar

# Give the non-root user ownership of the jar
RUN chown spring:spring app.jar

# Switch to the secure non-root user
USER spring:spring

# Expose the standard web port
EXPOSE 8080

# The command to start the engine
ENTRYPOINT ["java", "-jar", "app.jar"]