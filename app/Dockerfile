FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/Blog-0.0.1-SNAPSHOT.jar app.jar
COPY blog.properties blog.properties
ENTRYPOINT ["java", "-Dspring.config.location=/blog.properties", "-jar", "/app.jar"]