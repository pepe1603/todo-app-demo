# Build stage
FROM maven:21-eclipse-temurin-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Database (Aiven) - Replace with environment variables in production
ENV URL_DATABASE_POSTGRESQL=jdbc:postgresql://YOUR_HOST:YOUR_PORT/YOUR_DB?ssl=require
ENV DB_USERNAME=your_db_username
ENV DB_PASSWORD=your_db_password

# Mail (Resend) - Replace with environment variables in production
ENV MAIL_HOST=smtp.resend.com
ENV MAIL_PORT=587
ENV MAIL_USERNAME=resend
ENV MAIL_PASSWORD=your_resend_api_key
ENV MAIL_FROM=onboarding@resend.dev

# Redis (Upstash) - Replace with environment variables in production
ENV REDIS_HOST=your_upstash_host
ENV REDIS_PORT=6379
ENV REDIS_PASSWORD=your_upstash_password
ENV REDIS_SSL_ENABLED=true

# JWT - Replace with environment variables in production
ENV JWT_SECRET=your_jwt_secret_key_here
ENV JWT_ACCESS_TOKEN_EXPIRATION=900000

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9090

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]