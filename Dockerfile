# Build stage
FROM ubuntu:24.04 AS build
WORKDIR /app

# Update and install Maven and Java
RUN apt-get update && apt-get install -y \
    maven \
    openjdk-21-jdk \
    curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM ubuntu:24.04
WORKDIR /app

# Install Java JRE only
RUN apt-get update && apt-get install -y \
    openjdk-21-jre-headless && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9090

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]