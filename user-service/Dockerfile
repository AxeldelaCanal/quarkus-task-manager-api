# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Cache dependency resolution separately from source compilation.
# Both the parent pom and the module pom are needed because this is a
# multi-module Maven project — the parent pom defines dependency management.
COPY pom.xml .
COPY user-service/pom.xml user-service/
COPY task-service/pom.xml task-service/
RUN mvn dependency:go-offline -pl user-service -am -q

COPY user-service/src user-service/src
RUN mvn package -DskipTests -pl user-service -am -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Quarkus fast-jar layout produced by quarkus-maven-plugin
COPY --from=build /app/user-service/target/quarkus-app/lib/     ./lib/
COPY --from=build /app/user-service/target/quarkus-app/*.jar    ./
COPY --from=build /app/user-service/target/quarkus-app/app/     ./app/
COPY --from=build /app/user-service/target/quarkus-app/quarkus/ ./quarkus/

EXPOSE 8081
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0"

CMD ["sh", "-c", "java $JAVA_OPTS -jar quarkus-run.jar"]
