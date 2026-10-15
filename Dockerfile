# One service = API + frontend. Build context is the repo root.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY backend/pom.xml .
RUN mvn -q -B dependency:go-offline
COPY backend/src ./src
COPY frontend ./src/main/resources/static
RUN mvn -q -B package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/hostel.jar hostel.jar
COPY backend/docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]
