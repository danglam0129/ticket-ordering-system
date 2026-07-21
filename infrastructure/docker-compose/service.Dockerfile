FROM eclipse-temurin:17-jdk AS build

ARG SERVICE_MODULE
ARG JAR_FILE

WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -pl ${SERVICE_MODULE} -am package -DskipTests

FROM eclipse-temurin:17-jre

ARG JAR_FILE

WORKDIR /app
COPY --from=build /workspace/${JAR_FILE} /app/app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
