#
# maven maintenance
#
FROM gcr.io/som-laneweb/eresources:latest AS PREVIOUS_IMAGE
# use .m2 directory from previous image to speed-up build-times
# .m2 should already exist, but create just in case
RUN mkdir -p /root/.m2

#
# build phase
#
FROM maven:3 AS MAVEN_TOOL_CHAIN
COPY pom.xml settings.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
COPY --from=PREVIOUS_IMAGE /root/.m2 /root/.m2
RUN mvn -B -s settings.xml -DskipTests clean package
# purge maven dependencies that haven't been accessed recently
RUN find /root/.m2/repository -atime +30 -iname '*.pom' \
    | while read pom; do parent=`dirname "$pom"`; rm -Rf "$parent"; done

#
# run phase
#
FROM openjdk:14-alpine
RUN apk add --no-cache tcpdump sysstat tini curl
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/eresources.jar /eresources/eresources.jar
COPY --from=MAVEN_TOOL_CHAIN /root/.m2 /root/.m2
EXPOSE 8080
WORKDIR /eresources
RUN ln -s /eresources-config/application.properties application.properties
ENTRYPOINT ["/sbin/tini", "--"]
CMD "java" "-Duser.timezone=America/Los_Angeles" "-jar" "/eresources/eresources.jar"
