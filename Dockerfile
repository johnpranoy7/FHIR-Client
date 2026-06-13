# Stage 1: Build Vue frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Spring Boot app with static UI
FROM eclipse-temurin:17-jdk-alpine AS backend-build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN chmod +x mvnw && ./mvnw -q -DskipTests package

# Stage 3: Runtime image for Render
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
ENV PORT=8081
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
