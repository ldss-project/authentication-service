FROM eclipse-temurin:17-alpine

RUN apk update && apk add git
COPY ./ ./project
WORKDIR ./project
RUN chmod 777 ./gradlew && ./gradlew clean build
EXPOSE 8080/tcp

CMD ./gradlew run