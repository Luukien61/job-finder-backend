FROM openjdk:21-slim-bullseye
LABEL authors="luukien"
WORKDIR /app
COPY ./build/libs/job-finder-backend-0.0.1-SNAPSHOT.jar /app/backend.jar
EXPOSE 8088
ENTRYPOINT ["sh", "-c", "java", "-jar", "/app/backend.jar","--spring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}"]
#/job-finder-backend$ docker build -t luukien/jobfinder-backend:1.0 -f ./docker/Dockerfile .