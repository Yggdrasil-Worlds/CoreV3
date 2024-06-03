# Use an official Gradle image to build the application
FROM gradle:8.1.1-jdk-alpine AS TEMP_BUILD_IMAGE
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME

COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src

RUN gradle build || return 0
COPY . .
RUN gradle clean build

# Use an official OpenJDK runtime as a parent image
FROM openjdk:20-jdk-slim
ENV ARTIFACT_NAME=ServerCore.jar
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .
COPY .env $APP_HOME

# Expose the required port
EXPOSE 25565

# Start the application
ENTRYPOINT exec java -jar ${ARTIFACT_NAME}
