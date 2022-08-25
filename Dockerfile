#   
# run phase
#
#FROM gcr.io/som-laneweb/jre-parent:eclipse-temurin-11.0.16.1_1-jre
FROM eclipse-temurin:17-jre-jammy

RUN apt update && \
    apt-get -y install \
    net-tools \
    tini \
    procps \
    curl \
    fontconfig

VOLUME /config
VOLUME /secrets

RUN apt update && \
    apt-get -y install \
    net-tools \
    tini \
    procps \
    curl \
    fontconfig

VOLUME /config
VOLUME /secrets

ADD ./target/eresources.jar /eresources/eresources.jar

EXPOSE 8080

WORKDIR /eresources

RUN ln -s /config/application.properties application.properties

ENTRYPOINT ["/usr/bin/tini", "--"]
CMD  ["java", "-Duser.timezone=America/Los_Angeles", "-jar", "/eresources/eresources.jar"]

