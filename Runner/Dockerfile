FROM java:openjdk-8-jre

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Add project and run deps so this can be cached and only recreated if project.clj changes
ADD project.clj /usr/src/app/

RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && \
chmod a+x ./lein
RUN LEIN_ROOT="true" ./lein deps
ADD . /usr/src/app
RUN LEIN_ROOT="true" ./lein uberjar

ENV PORT_LOCAL 80
EXPOSE 80
CMD java -jar /usr/src/app/target/uberjar/cicada-chaingun-runner.jar
