FROM us-docker.pkg.dev/som-laneweb/docker-private/jre-parent:eclipse-temurin-21.0.4_7-jre

ADD ./target/eresources.jar /eresources/eresources.jar

EXPOSE 8080

WORKDIR /eresources

RUN ln -s /config/application.properties application.properties

ENTRYPOINT ["/usr/bin/tini", "--"]
CMD  ["java", "-Duser.timezone=America/Los_Angeles", "-jar", "/eresources/eresources.jar"]
