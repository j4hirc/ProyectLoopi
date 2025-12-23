FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -Dmaven.wagon.http.retryHandler.count=3 -Dmaven.wagon.http.retryHandler.class=standard
COPY src src
RUN ./mvnw package -DskipTests -Dmaven.wagon.http.retryHandler.count=3
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar app.jar
EXPOSE 8080 

ENTRYPOINT ["java","-jar","app.jar"]
