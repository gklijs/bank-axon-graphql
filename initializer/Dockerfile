FROM azul/zulu-openjdk-alpine:11-jre
MAINTAINER Gerard Klijs <gerard@openweb.nl>

ADD target/initializer-docker.jar /app.jar

CMD ["java", "-Xmx1024m", "-jar", "/app.jar"]