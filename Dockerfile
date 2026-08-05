FROM maven:3-eclipse-temurin-25-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:25-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup \
    && apk add --no-cache curl

WORKDIR /app
COPY --from=build /app/target/tesoreria-compras-service.jar ./tesoreria-compras-service.jar

RUN chown -R appuser:appgroup /app
USER appuser

ENTRYPOINT ["java", "-jar", "tesoreria-compras-service.jar"]
