FROM gradle:8.14-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle :nolzo-api:bootJar --no-daemon

FROM amazoncorretto:21
VOLUME /tmp
COPY --from=builder /app/nolzo-api/build/libs/nolzo-api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
