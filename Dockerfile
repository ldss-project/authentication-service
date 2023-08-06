FROM eclipse-temurin:17-alpine

COPY ./build/libs/*.jar ./app/app.jar

WORKDIR ./app
EXPOSE 8080/tcp

ENTRYPOINT ["java", "-jar", "app.jar"]