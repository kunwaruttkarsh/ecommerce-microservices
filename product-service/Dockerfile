FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY ../security-lib /security-lib
RUN cd /security-lib && mvn clean install -DskipTests
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]