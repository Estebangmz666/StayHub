# Etapa 1: Construir el JAR con Maven + Java 21
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime ligero con Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar solo el JAR
COPY --from=builder /app/target/*.jar app.jar

# Puerto de Render
EXPOSE 10000

# Arrancar
ENTRYPOINT ["java", "-jar", "app.jar"]