FROM bellsoft/liberica-openjre-alpine:17

ARG JAR_FILE=build/libs/qastore-api.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]