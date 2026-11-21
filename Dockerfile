FROM gradle:8.14-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle :nolzo-api:bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --uid 10001 --create-home appuser
VOLUME /tmp
COPY --from=builder /app/nolzo-api/build/libs/nolzo-api-0.0.1-SNAPSHOT.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "/app.jar"]
