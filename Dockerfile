#   
# run phase
#
FROM gcr.io/som-laneweb/jre-parent:openjdk-11.0.14-jre-slim


ADD ./target/eresources.jar /eresources/eresources.jar

EXPOSE 8080

WORKDIR /eresources

RUN ln -s /config/application.properties application.properties

ENTRYPOINT ["/usr/bin/tini", "--"]
CMD  ["java", "-Duser.timezone=America/Los_Angeles", "-jar", "/eresources/eresources.jar"]

