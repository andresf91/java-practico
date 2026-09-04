FROM quay.io/wildfly/wildfly:41.0.0.Final-jdk21

RUN /opt/jboss/wildfly/bin/add-user.sh admin 'Admin#2026' --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u tse -p 'Tse#2026' -g guest --silent

ADD --chown=jboss:root https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.13/postgresql-42.7.13.jar /tmp/postgresql.jar
COPY --chown=jboss:root docker/wildfly/postgres.cli /tmp/postgres.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/postgres.cli \
    && rm -rf /tmp/postgres.cli /opt/jboss/wildfly/standalone/configuration/standalone_xml_history

COPY ear/target/practicojava.ear /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", \
     "-c", "standalone-full.xml", \
     "-b", "0.0.0.0"]
