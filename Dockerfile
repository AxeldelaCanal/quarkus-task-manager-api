# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Cache dependency resolution separately from source compilation
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Quarkus fast-jar layout
COPY --from=build /app/target/quarkus-app/lib/           ./lib/
COPY --from=build /app/target/quarkus-app/*.jar          ./
COPY --from=build /app/target/quarkus-app/app/           ./app/
COPY --from=build /app/target/quarkus-app/quarkus/       ./quarkus/

EXPOSE 8080
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0"

CMD ["sh", "-c", "java $JAVA_OPTS -jar quarkus-run.jar"]
