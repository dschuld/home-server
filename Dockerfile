FROM openjdk:21-jdk-slim
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /app

RUN apt-get update && apt-get install -y mpg123

COPY build/libs/*-all.jar app.jar
COPY build/resources/main/tng_red_alert1.mp3 /app/tng_red_alert1.mp3
COPY build/resources/main/pausengong.mp3 /app/pausengong.mp3
EXPOSE 7077
VOLUME /data
ENTRYPOINT ["java", "-jar", "app.jar"]
