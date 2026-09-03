# etapa de compilación, genera el ear desde el fuente
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
COPY client/pom.xml client/
COPY data data
COPY ejb ejb
COPY web web
COPY ear ear

# -pl ear -am construye data, ejb y web, omite el cliente de consola
RUN mvn -B -DskipTests -pl ear -am package

# etapa de ejecución, wildfly con el ear ya construido
FROM quay.io/wildfly/wildfly:41.0.0.Final-jdk21

RUN /opt/jboss/wildfly/bin/add-user.sh admin 'Admin#2026' --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u tse -p 'Tse#2026' -g guest --silent

COPY --from=build /build/ear/target/practicojava.ear /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", \
     "-c", "standalone-full.xml", \
     "-b", "0.0.0.0"]
