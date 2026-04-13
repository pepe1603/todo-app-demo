# Build stage
FROM amazoncorretto:21 AS build
WORKDIR /app

# Install Maven 3.9+
RUN curl -sL https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz | tar -xz && \
    mv apache-maven-3.9.9 /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/bin/mvn

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:21
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9090

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]