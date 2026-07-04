# ============================================================
# Stage 1: Build — Compila o JAR com Maven
# ============================================================
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# 1. Copiar Maven Wrapper e POM (cacheia dependências)
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# 2. Copiar código-fonte e compilar (sem testes — já rodaram no CI)
COPY src src
RUN ./mvnw clean package -DskipTests -B

# 3. Extrair layers do Spring Boot para cache eficiente
RUN java -Djarmode=layertools -jar target/*.jar extract --destination /app/extracted

# ============================================================
# Stage 2: Runtime — Apenas JRE + App
# ============================================================
FROM eclipse-temurin:21-jre-jammy

# Segurança: criar e usar usuário não-root
RUN groupadd -r mitra && useradd -r -g mitra -d /app -s /sbin/nologin mitra

WORKDIR /app

# Copiar layers extraídas (ordem = menos mutável -> mais mutável)
COPY --from=builder --chown=mitra:mitra /app/extracted/dependencies/ ./
COPY --from=builder --chown=mitra:mitra /app/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=mitra:mitra /app/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=mitra:mitra /app/extracted/application/ ./

USER mitra

# JVM otimizada para containers
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
