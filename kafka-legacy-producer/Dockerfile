FROM azul/zulu-openjdk-alpine:11-jre
MAINTAINER Gerard Klijs <gerard.klijs@axoniq.io>

ADD target/legacy-producer-docker-jar-with-dependencies.jar /app.jar

CMD ["java", "-Xmx512m", "-jar", "/app.jar"]