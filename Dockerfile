# Etapa 1: Build da aplicação Java com Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia pom.xml para cache das dependências
COPY pom.xml .
RUN mvn dependency:resolve-plugins dependency:go-offline -B || true

# Copia o código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final leve (JRE Alpine)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
