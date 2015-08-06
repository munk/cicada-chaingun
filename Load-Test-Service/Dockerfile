FROM java:openjdk-8-jre
MAINTAINER John Downs <john.downs@yodle.com>

COPY target/uberjar/load-test-service-0.1.0-SNAPSHOT-standalone.jar load-test-service-0.1.0-SNAPSHOT-standalone.jar

ENV PORT 4000

EXPOSE 4000

CMD ["java", "-jar", "load-test-service-0.1.0-SNAPSHOT-standalone.jar"]
