FROM azul/zulu-openjdk-alpine:11-jre
MAINTAINER Gerard Klijs <gerard.klijs@axoniq.io>

ADD target/legacy-consumer-docker.jar /app.jar

CMD ["java", "-Xmx1024m", "-jar", "/app.jar"]