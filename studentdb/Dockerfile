# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/studentdb-1.0-SNAPSHOT.jar app.jar
COPY --from=build /root/.m2 /root/.m2

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-cp", "app.jar:~/.m2/repository/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar", "com.lab.WebServerApp"]