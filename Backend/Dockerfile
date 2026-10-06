# ----- Stage 1: Build -----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# ----- Stage 2: Runtime -----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Adiciona usuário não-root (hardening de segurança)
RUN addgroup -S shopflow && adduser -S shopflow -G shopflow
USER shopflow

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
