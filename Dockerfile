#   
# run phase
#
FROM gcr.io/som-laneweb/jre-parent:prod-latest


ADD ./target/eresources.jar /eresources/eresources.jar

EXPOSE 8080

WORKDIR /eresources

RUN ln -s /config/application.properties application.properties

ENTRYPOINT ["/usr/bin/tini", "--"]
CMD  ["java", "-Duser.timezone=America/Los_Angeles", "-jar", "/eresources/eresources.jar"]

