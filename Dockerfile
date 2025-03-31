FROM openjdk:17-jdk-slim
WORKDIR /app

COPY build/libs/*SNAPSHOT.jar app.jar
COPY src/main/resources/data/GeoLite2-City.mmdb /app/data/GeoLite2-City.mmdb

# 추가
RUN mkdir /app/settings
RUN chmod +r /app/data/GeoLite2-City.mmdb

CMD ["java", "-jar", "-Dspring.profiles.active=dev", "app.jar"]
