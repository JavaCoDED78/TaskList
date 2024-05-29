FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
COPY checkstyle-suppressions.xml ./
RUN mvn -f /app/pom.xml clean package

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/TaskList-0.0.1-SNAPSHOT-exec.jar ./application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]