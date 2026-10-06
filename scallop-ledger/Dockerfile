# ============================================
# Build Stage
# ============================================
FROM maven:3.9-eclipse-temurin-26 AS build

WORKDIR /src

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src ./src

RUN mvn -B clean package -DskipTests


# ============================================
# Runtime Stage
# ============================================
FROM eclipse-temurin:26-jre

RUN groupadd --gid 10001 ledger \
    && useradd --uid 10001 \
       --gid ledger \
       --no-create-home \
       --shell /usr/sbin/nologin \
       ledger

WORKDIR /app

COPY --from=build \
    /src/target/scallop-ledger-2.6.0.jar \
    /app/app.jar

RUN chown ledger:ledger /app/app.jar

USER ledger

EXPOSE 8089

ENTRYPOINT ["java","-XX:MaxRAMPercentage=70","-jar","/app/app.jar"]