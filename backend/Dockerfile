# build
FROM amazoncorretto:21-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN chmod +x ./gradlew

RUN ./gradlew clean bootJar -x test

# run
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN wget -O agent.jar https://github.com/grafana/grafana-opentelemetry-java/releases/latest/download/grafana-opentelemetry-java.jar
ENTRYPOINT ["java", "-javaagent:agent.jar", "-Dspring.profiles.active=prod", "-jar", "app.jar"]