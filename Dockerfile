FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/*-all.jar app.jar
EXPOSE 7077
ENTRYPOINT ["java", "-jar", "app.jar"]
