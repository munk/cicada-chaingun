FROM java:openjdk-8-jre
MAINTAINER John Downs <john.downs@yodle.com>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Add project and run deps so this can be cached and only recreated if project.clj changes
ADD project.clj /usr/src/app/

RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && \
chmod a+x ./lein
RUN LEIN_ROOT="true" ./lein deps
ADD . /usr/src/app
RUN LEIN_ROOT="true" ./lein uberjar

ENV PORT 4000

EXPOSE 4000

CMD java -jar /usr/src/app/target/uberjar/load-test-service-0.1.0-SNAPSHOT-standalone.jar
