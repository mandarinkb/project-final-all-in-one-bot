# Use official base image of Java Runtim
FROM openjdk:8-jdk-alpine

#change timezone
#RUN date
#RUN apk add tzdata
#RUN cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime
#RUN date

RUN date && \
    apk add tzdata && \
    cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime && \
    date

# Set volume point to /tmp
VOLUME /tmp

# Make port 8081 available to the world outside container
EXPOSE 8084

# Set application's JAR file
ARG JAR_FILE=project-final-all-in-one-bot-0.0.1-SNAPSHOT.jar

# Add the application's JAR file to the container
ADD ${JAR_FILE} app.jar

# Run the JAR file
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]