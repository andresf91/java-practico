# java-practico
Práctico Práctico TSE Java 2026


Gestor de **Trabajadores de la Salud** (modelo de entidades principales de Salud.uy / AGESIC), implementado como aplicación Jakarta EE de tres capas y desplegado en WildFly.

## Módulos

practicojava/
- data/ (Session Bean singleton + entidad + intfz de datos)
- ejb/ (Session Bean stateless + intfz de negocio + excepciones)
- web/ (Servlet + JSP + JSF con PrimeFaces)
- ear/ (empaqueta data+ejb+web)
- client/ (cliente de consola)

## Entidad

- id (Long)
- numeroRegistroMSP (String, único)
- especialidad (String)
- fechaAlta (LocalDate, no futura)
- aniosExperiencia (int, entre 0 y 60)
- prestadores (List< String >)


## 1. Levantar WildFly (Docker)

```bash
docker build -t tse-wildfly:41 docker/wildfly
docker run -d --name tse-wildfly -p 8080:8080 -p 9990:9990 -v "$PWD/deployments":/opt/jboss/wildfly/standalone/deployments tse-wildfly:41
```

Copiar el .ear en deployments/ para que Wildfly lo despliegue.

Credenciales: consola de administración: admin/Admin#2026 (http://localhost:9990).
Usuario de aplicación tse/Tse#2026 (grupo guest) para cliente de consola.

## 2. Compilar y desplegar

```bash
mvn clean install
cp ear/target/practicojava.ear deployments/
docker logs -f tse-wildfly
```


## 3. Probar

- Portada: http://localhost:8080/practicojava/
- Web JSF: http://localhost:8080/practicojava/trabajadores.xhtml
- Web Servlet: http://localhost:8080/practicojava/trabajadores
- Consola: `mvn -pl client exec:java`

## 4. Deploy en Google Cloud Run

```bash
mvn clean install
gcloud run deploy practicojava \
  --source . \
  --region southamerica-east1 \
  --port 8080 \
  --memory 1Gi --cpu 1 \
  --max-instances 1 \
  --allow-unauthenticated
```

Agregar `--min-instances 1` para tener una instancia siempre viva y se pierda el estado de memoria en data.