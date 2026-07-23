FROM eclipse-temurin:23-jdk

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

CMD ["java", "-jar", "backend/build/libs/backend-0.0.1-SNAPSHOT.jar"]