#Build the application
FROM maven:3.8.4-openjdk-17 AS BUILD
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run the application
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/target/dental-clinic-scheduling-platform-0.0.1-SNAPSHOT.jar ./dental-clinic-be.jar
EXPOSE 8080
CMD ["java", "-jar", "dental-clinic-be.jar"]