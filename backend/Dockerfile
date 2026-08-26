# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# Cache de dependencias: copia so o pom primeiro
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine

# Usuario nao-root
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
RUN chown spring:spring /app/app.jar

# Os SQLs tambem ficam soltos na imagem, alem de empacotados no jar.
# O Job de migration do Helm copia daqui para o container do Flyway CLI,
# garantindo que schema e aplicacao venham sempre da mesma tag de imagem.
COPY --from=build /build/src/main/resources/db/migration /migrations

USER spring

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
