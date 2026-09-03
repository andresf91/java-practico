FROM quay.io/wildfly/wildfly:41.0.0.Final-jdk21

# usuario de la consola de administración (ManagementRealm)
RUN /opt/jboss/wildfly/bin/add-user.sh admin 'Admin#2026' --silent

# usuario de aplicación (ApplicationRealm) en el grupo guest
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u tse -p 'Tse#2026' -g guest --silent

EXPOSE 8080 9990

# standalone-full.xml: el perfil por defecto no trae el subsistema de mensajería
CMD ["/opt/jboss/wildfly/bin/standalone.sh", \
     "-c", "standalone-full.xml", \
     "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
