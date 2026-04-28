# MinData RIU Challenge

## Descripción

API REST desarrollada con Java 21 y Spring Boot 3, basada en arquitectura hexagonal, con Apache Kafka para desacoplar la persistencia y MySQL como base de datos.

El objetivo del proyecto es resolver un take-home challenge para una posición Backend Java, priorizando claridad de diseño, separación de responsabilidades y una solución fácil de ejecutar y validar.

## Funcionalidad requerida

- Registrar búsquedas de hotel mediante `POST /search`
- Consultar cuántas búsquedas persistidas son exactamente iguales mediante `GET /count`
- Considerar el orden de `ages` como parte de la igualdad
- Validar requests con mensajes claros en caso de error
- Explorar y probar la API desde Swagger UI
- Levantar el entorno completo con `docker compose`

## Cómo levantar el entorno

La aplicación se entrega con un `docker compose` que permite levantar rápidamente todo el entorno necesario, sin requerir instalación local de Java, Maven, MySQL ni Kafka.

```bash
# Compila y levanta todos los servicios en segundo plano
docker compose up --build -d

# Muestra sólo los logs de la aplicación
docker compose logs -f app

# Detiene los servicios
docker compose down

# Detiene y elimina también el volumen de MySQL
docker compose down -v
```

Levantar el entorno en segundo plano permite seguir sólo los logs de la aplicación, evitando el ruido de Kafka y MySQL durante las pruebas manuales.

Servicios disponibles una vez levantado el entorno:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- MySQL: `localhost:3307`
- Kafka: `localhost:9092`

## Documentación de la API

Con la aplicación en ejecución, la documentación interactiva está disponible en:

```text
http://localhost:8080/swagger-ui/index.html
```

También se expone la especificación OpenAPI en:

```text
http://localhost:8080/v3/api-docs
```

## API disponible

| Método | Path | Descripción |
|---|---|---|
| POST | `/search` | Registra una búsqueda y devuelve un `searchId` único |
| GET | `/count` | Recupera una búsqueda persistida y devuelve cuántas búsquedas iguales existen |

La documentación completa de requests, responses y códigos HTTP queda disponible en Swagger.

Contrato de fechas:

- `checkIn` y `checkOut` se reciben y se devuelven en formato `dd/MM/yyyy`
- internamente la aplicación sigue trabajando con `LocalDate`

## Reglas de validación

La API aplica validaciones tanto a nivel HTTP como de dominio. Entre las reglas principales:

- `hotelId` no puede ser vacío
- `checkIn` y `checkOut` son obligatorios
- `checkIn` y `checkOut` deben enviarse en formato `dd/MM/yyyy`
- `checkIn` debe ser anterior a `checkOut`
- `ages` no puede ser vacío
-  las edades deben ser mayores o iguales a `0`
- `searchId` es obligatorio en `GET /count`

Las requests inválidas devuelven `400 Bad Request` con mensajes claros. Si se consulta un `searchId` que todavía no existe en persistencia, la API devuelve `404 Not Found`.

## Cómo validar el comportamiento esperado

Para facilitar la validación funcional de la solución, se incluyen archivos auxiliares con payloads de ejemplo y consultas para inspeccionar la persistencia.

Referencias útiles:

- `examples/manual-validation-guide.md`

La validación principal que conviene revisar es:

- dos búsquedas idénticas deben contarse juntas
- una búsqueda con el mismo contenido pero distinto orden en `ages` debe considerarse distinta
- cada `POST /search` debe devolver un `searchId` distinto, incluso con payloads idénticos
- un `GET /count` ejecutado inmediatamente después de `POST /search` puede devolver transitoriamente `404` hasta que el consumidor procese el mensaje

## Variables de entorno

La aplicación admite configuración por variables de entorno. Si se utiliza `docker compose`, no hace falta definirlas manualmente, porque ya están configuradas en el propio entorno dockerizado.

En ejecución local, `KAFKA_CONSUMER_ENABLED` vale `false` por defecto. Esto permite arrancar la aplicación sin activar el consumidor Kafka, a menos que se quiera probar explícitamente el flujo completo de publicación, consumo y persistencia.

En `docker compose`, MySQL se expone en el puerto `3307` del host para evitar conflictos con instalaciones locales que ya utilicen `3306`. Internamente, el contenedor sigue usando `3306`.

| Variable | Uso | Default local |
|---|---|---|
| `KAFKA_BOOTSTRAP_SERVERS` | broker Kafka | `localhost:9092` |
| `KAFKA_SEARCH_TOPIC` | topic de publicación/consumo | `hotel_availability_searches` |
| `KAFKA_SEARCH_CONSUMER_GROUP_ID` | consumer group del listener | `hotel-availability-searches-persistence` |
| `KAFKA_CONSUMER_ENABLED` | habilita el consumidor | `false` |
| `MYSQL_HOST` | host MySQL | `localhost` |
| `MYSQL_PORT` | puerto MySQL | `3306` |
| `MYSQL_DATABASE` | base de datos | `mindata_riu_challenge` |
| `MYSQL_USER` | usuario MySQL | `root` |
| `MYSQL_PASSWORD` | password MySQL | `root` |

## Arquitectura de la solución

Se planteó una arquitectura hexagonal estricta, separando claramente dominio, aplicación e infraestructura para evitar mezclar responsabilidades entre capas.

- `domain`: contiene el modelo de negocio, las reglas de validación y los objetos que representan la búsqueda
- `application`: define los casos de uso y los puertos necesarios para orquestar el flujo
- `infrastructure`: implementa los adapters de entrada y salida, incluyendo controllers REST, Kafka y persistencia

La organización del código principal sigue esta estructura:

```text
src/main/java/com/grietm/challenge
|-- application
|-- domain
`-- infrastructure
```

A nivel de dependencias:

- `domain` no depende de otras capas
- `application` depende sólo de `domain`
- `infrastructure` conecta el sistema con Spring, Kafka y la base de datos

## Decisiones técnicas relevantes

Algunas decisiones relevantes de la implementación:

- Se utilizaron objetos inmutables para evitar cambios accidentales de estado durante el procesamiento de cada búsqueda.
- La capa de aplicación se mantuvo como Java puro, sin anotaciones de Spring como `@Service`, para evitar acoplarla al framework.
- El `searchId` se genera de forma aleatoria y se asigna a cada búsqueda de manera independiente, incluso cuando varias búsquedas tienen exactamente el mismo contenido.
- Se utilizaron modelos distintos para HTTP, dominio, mensajería y persistencia, evitando acoplar una capa a otra. 
- La regla de igualdad entre búsquedas considera también el orden de `ages`, tal como pide el challenge.
- La persistencia del `POST /search` es asíncrona: la API publica el evento en Kafka y el consumidor lo almacena luego en MySQL.
- El conteo se resuelve con una query parametrizada, evitando construir SQL mediante concatenación manual de valores y reduciendo el riesgo de SQL injection.

## Tecnologías utilizadas

| Componente | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 3.5.13 |
| Mensajería | Apache Kafka | 3.9.0 |
| Base de datos | MySQL | 8.4 |
| Persistencia | Spring Data JPA / Hibernate | incluida en Spring Boot |
| Documentación | Springdoc OpenAPI / Swagger UI | 2.8.8 |
| Testing | JUnit 5 / Mockito / Spring Test | incluido en Spring Boot Starter Test |
| Base de datos para tests | H2 | incluida como dependencia de test |
| Cobertura | JaCoCo | 0.8.12 |
| Build | Maven Wrapper | incluido |
| Contenedores | Docker / Docker Compose | runtime local |

## Testing y cobertura

La estrategia de testing combina:

- tests unitarios de dominio y aplicación
- tests web con `@WebMvcTest`
- tests de persistencia con Spring y H2

Para ejecutar la suite completa y generar el reporte de JaCoCo:

```bash
./mvnw verify
```

En Windows PowerShell:

```powershell
.\mvnw.cmd verify
```

Este comando ejecuta la suite de tests y genera el reporte de cobertura JaCoCo.

El reporte se genera localmente en:

```text
target/site/jacoco/index.html
```

Según la última ejecución disponible del reporte JaCoCo:

- Line coverage: `86%`
- Branch coverage: `63%`

La cobertura de líneas supera el objetivo mínimo del challenge, mientras que la cobertura de branches sigue siendo uno de los puntos a mejorar.

## Cobertura de requisitos del challenge

La solución cubre los principales requisitos solicitados en el challenge:

- uso de Java 21 y Spring Boot 3
- arquitectura hexagonal con separación entre `domain`, `application` e `infrastructure`
- endpoints `POST /search` y `GET /count`
- uso de `LocalDate`
- objetos inmutables en el modelo
- validaciones con `400 Bad Request` y mensajes claros
- `searchId` único por cada búsqueda
- igualdad sensible al orden de `ages`
- separación entre productor y consumidor Kafka
- documentación OpenAPI / Swagger
- entorno completamente dockerizado con `docker compose`
- query parametrizada para evitar concatenación manual de SQL

Notas:

- Se utilizó MySQL en lugar de Oracle, priorizando una tecnología con la que tengo mayor familiaridad que igualmente permita levantar y validar el entorno completo de forma ágil, sin afectar el objetivo funcional de la solución.
- No se incorporaron hilos virtuales porque se priorizó enfocar el tiempo de implementación en los requisitos principales del challenge, sin sumar complejidad adicional en una parte que no representaba una necesidad técnica clara para este caso.

## Mejoras posibles

- Elevar branch coverage hasta el umbral objetivo del challenge
- Incorporar reintentos o una estrategia de recuperación para errores de consumo en Kafka
- Mejorar la observabilidad con logs más orientados al seguimiento del flujo entre publicación, consumo y persistencia
- Evaluar el uso de virtual threads si el volumen de concurrencia crece y se vuelve necesario optimizar el manejo de tareas bloqueantes
