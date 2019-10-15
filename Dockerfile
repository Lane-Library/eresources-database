FROM openjdk:jre-alpine

RUN apk add --no-cache tcpdump sysstat tini

ADD target/eresources.jar /eresources/eresources.jar

EXPOSE 8080

WORKDIR /eresources

RUN ln -s /eresources-config/application.properties application.properties

ENTRYPOINT ["/sbin/tini", "--"]

CMD "java" "-Duser.timezone=America/Los_Angeles" "-jar" "/eresources/eresources.jar"
