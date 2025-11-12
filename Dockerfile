# ================================
# Stage 1: Build (Maven + JDK)
# ================================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia pom.xml e baixa dependências (cache de build)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o código-fonte
COPY src ./src

# Roda testes + build (vai respeitar JaCoCo e suas regras)
RUN mvn -B clean package -DskipTests=false

# ================================
# Stage 2: Runtime (JDK leve)
# ================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o jar gerado do stage de build
COPY --from=build /app/target/*.jar app.jar

# Porta exposta (Render ignora EXPOSE, mas ajuda localmente)
EXPOSE 8080

# Variáveis opcionais (profile, timezone, etc.)
ENV JAVA_OPTS=""

# Comando de entrada
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
