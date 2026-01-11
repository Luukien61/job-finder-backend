FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY ./build/libs/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "/app/app.jar","--spring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}"]
#/job-finder-backend$ docker build -t luukien/jobfinder-backend:1.0 -f ./docker/Dockerfile .