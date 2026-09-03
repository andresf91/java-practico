FROM quay.io/wildfly/wildfly:41.0.0.Final-jdk21

RUN /opt/jboss/wildfly/bin/add-user.sh admin 'Admin#2026' --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u tse -p 'Tse#2026' -g guest --silent

COPY ear/target/practicojava.ear /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", \
     "-c", "standalone-full.xml", \
     "-b", "0.0.0.0"]
