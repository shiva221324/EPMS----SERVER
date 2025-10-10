FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

# Make sure Unix mvnw is executable
RUN chmod +x mvnw

# Build with Unix wrapper
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/Payroll-Management-System-0.0.1-SNAPSHOT.jar"]
