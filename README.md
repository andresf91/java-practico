# java-practico
Práctico Práctico TSE Java 2026


Gestor de **Trabajadores de la Salud** (modelo de entidades principales de Salud.uy / AGESIC), implementado como aplicación Jakarta EE de tres capas y desplegado en WildFly.

## Módulos

practicojava/
- data/ (Session Bean singleton + entidad + intfz de datos)
- ejb/ (Session Bean stateless + intfz de negocio + excepciones)
- web/ (Servlet + JSP)
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

- Web: http://localhost:8080/practicojava/trabajadores
- Consola: `mvn -pl client exec:java`

## 4. Deploy en Google Cloud Run

El `Dockerfile` de la raíz es multi etapa. La primera etapa compila el EAR con Maven y la segunda lo
copia sobre WildFly, así que no hace falta construir nada antes de desplegar.

```bash
gcloud run deploy practicojava \
  --source . \
  --region southamerica-east1 \
  --port 8080 \
  --memory 1Gi --cpu 1 \
  --max-instances 1 \
  --allow-unauthenticated
```

Agregar `--min-instances 1` para tener una instancia siempre viva y que no se pierda el estado de
memoria en data.

## 5. CI/CD

Cada push a `main` o a `ej3` dispara el despliegue automático, sin intervención manual.

```
push a GitHub  ->  webhook  ->  Cloud Build  ->  Artifact Registry  ->  Cloud Run
```

Las piezas son:

- `cloudbuild.yaml`, el pipeline. Construye la imagen, la publica etiquetada con el SHA del commit y
  crea una revisión nueva del servicio de Cloud Run.
- Una conexión de Cloud Build con GitHub (`practicojava-github`, en `southamerica-east1`). Al
  vincular el repositorio, Cloud Build instala su GitHub App y crea el webhook. No hay claves
  guardadas en el repositorio.
- Un trigger (`practicojava-deploy`) que escucha los push a `main` y `ej3`.
- Una cuenta de servicio propia, `cloudbuild-deployer`, con permisos acotados. Escritura sobre el
  repositorio `practicojava` de Artifact Registry, administración del servicio de Cloud Run y uso de
  la cuenta de servicio de runtime.

Comandos de referencia para recrear la conexión y el trigger:

```bash
gcloud builds connections create github practicojava-github --region=southamerica-east1
gcloud builds repositories create java-practico \
  --remote-uri=https://github.com/andresf91/java-practico.git \
  --connection=practicojava-github --region=southamerica-east1
gcloud builds triggers create github \
  --name=practicojava-deploy \
  --repository=projects/proyecto-ti-493403/locations/southamerica-east1/connections/practicojava-github/repositories/java-practico \
  --branch-pattern='^(main|ej3)$' \
  --build-config=cloudbuild.yaml \
  --service-account=projects/proyecto-ti-493403/serviceAccounts/cloudbuild-deployer@proyecto-ti-493403.iam.gserviceaccount.com \
  --region=southamerica-east1
```

Estado de los builds y de las revisiones desplegadas:

```bash
gcloud builds list --region=southamerica-east1 --limit=5
gcloud run revisions list --service=practicojava --region=southamerica-east1
```

Cada despliegue crea una revisión nueva, por lo que el estado que guarda en memoria el Singleton de
la capa de datos se pierde en cada entrega. Es una consecuencia directa de no tener persistencia
hasta el Ejercicio 7.