FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY lib/ lib/
COPY src/ src/

# Build application
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests && \
    mv target/*.jar app.jar && \
    rm -rf target pom.xml src/ && \
    apk del maven

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/calculator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
