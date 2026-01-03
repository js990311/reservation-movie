# build
FROM amazoncorretto:21-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootJar -x test --no-daemon

# run
FROM amazoncorretto:21-alpine
WORKDIR /app

RUN wget -O agent.jar https://github.com/grafana/grafana-opentelemetry-java/releases/latest/download/grafana-opentelemetry-java.jar

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-javaagent:agent.jar", "-Dspring.profiles.active=prod", "-jar", "app.jar"]