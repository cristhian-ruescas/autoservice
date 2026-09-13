# Na raiz do repositório: docker compose -f docker/docker-compose.yaml build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl \
    && curl -fsSL -o /app/dd-java-agent.jar https://dtdg.co/latest-java-tracer

COPY --from=build /build/target/autoservice-*.jar /app/app.jar

EXPOSE 8088
ENTRYPOINT ["sh", "-c", "if [ -n \"${DD_API_KEY:-}\" ]; then exec java -javaagent:/app/dd-java-agent.jar ${JAVA_OPTS:-} -jar /app/app.jar; else exec java ${JAVA_OPTS:-} -jar /app/app.jar; fi"]
