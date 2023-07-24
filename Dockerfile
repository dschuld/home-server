FROM openjdk:17-jdk-slim
WORKDIR /app

RUN apt-get update && apt-get install -y mpg123

COPY build/libs/*-all.jar app.jar
EXPOSE 7077
ENTRYPOINT ["java", "-jar", "app.jar"]
