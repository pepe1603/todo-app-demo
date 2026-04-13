# Build stage
FROM amazoncorretto:21 AS build
WORKDIR /app

# Install Maven
RUN yum install -y maven

COPY pom.xml .
COPY src ./src

# Build without hardcoded credentials - use system env vars
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:21
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9090

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]