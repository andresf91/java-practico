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
docker run -d --name tse-wildfly --hostname localhost -p 8080:8080 -p 9990:9990 -v "$PWD/deployments":/opt/jboss/wildfly/standalone/deployments tse-wildfly:41
```

Copiar el .ear en deployments/ para que Wildfly lo despliegue.

--hostname localhost es necesario para el cliente JMS remoto.

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

## 5. Alta asincrónica con JMS

Formato del mensaje:

```
numeroRegistroMSP|nombreCompleto|especialidad|fechaAlta|aniosExperiencia|prestadores
MSP-2001|Diego Rocha|Neumología|2020-05-14|9|214771230011,215558820013
```

La fecha va en formato AAAA-MM-DD y los prestadores separados por coma.

Para pedir un alta por esta vía:
- Web JSF: botón **Encolar alta (asincrónico)**.
- Web Servlet: el mismo formulario con el botón *Encolar alta (asincrónico)*.
- Consola: opción 4 del menú.

## 6. Servicios web SOAP y REST

Los servicios viven en el WAR y delegan en la capa de negocio.

### SOAP

WSDL en http://localhost:8080/practicojava/GestorTrabajadoresService?wsdl

Operaciones: 
- listarTrabajadores
- buscarPorEspecialidad
- obtenerPorRegistroMSP
- agregarTrabajador

Las reglas de negocio incumplidas vuelven como SOAP Fault.

### REST

Base http://localhost:8080/practicojava/rest/trabajadores

#### Métodos GET
- /trabajadores (lista completa)
- /trabajadores?especialidad=Cardio (lista filtrada)
- /trabajadores/{numeroRegistroMSP} (detalle de un trabajador, o 404 si no existe)


#### Métodos POST
- /trabajadores (201 con cabecera **Location**, 400 si el dato es inválido, 409 si el registro MSP ya existe)
- /trabajadores/encolar (202, el alta la resuelve el MDB)

Representaciones JSON y XML según la cabecera **Accept**, 406 para cualquier otra.

#### Ejemplos con curl

```bash
curl http://localhost:8080/practicojava/rest/trabajadores
curl -H "Accept: application/xml" http://localhost:8080/practicojava/rest/trabajadores/MSP-1001
curl -X POST http://localhost:8080/practicojava/rest/trabajadores \
  -H "Content-Type: application/json" \
  -d '{"numeroRegistroMSP":"MSP-2001","nombreCompleto":"Diego Rocha","especialidad":"Neumología","fechaAlta":"2020-05-14","aniosExperiencia":9,"prestadores":["214771230011"]}'
```
