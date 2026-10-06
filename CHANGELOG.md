# Changelog

Formato: [Keep a Changelog](https://keepachangelog.com/es/1.1.0/) ·
Versionado: [SemVer](https://semver.org/lang/es/)

La versión vive en [`VERSION`](VERSION) y en el `<version>` del `pom.xml`; mantener los dos
sincronizados: `VERSION` es lo que lee el flujo de release, `pom.xml` lo que lee Maven.

---

## [0.3.0] - 2026-10-06

Integración con el resto del ecosistema. El servicio queda con los slices de lectura
contra `core-service`.

### Agregado

- Slice `articulo`: consume el maestro de artículos de `core-service` por Feign
  (`GET /api/tesoreria/core/articulo/{id}` y `POST /api/tesoreria/core/articulo/search`),
  con puerto de salida `ArticuloGateway`, adapter que traduce los errores de Feign a
  excepciones de dominio (`404`/`503`) y endpoints propios en
  `/api/tesoreria/compras/articulo`.
- `ApiKeyFilter`, como umhub: todos los endpoints exigen el header `X-API-Key` con la
  clave de `app.api-key` (`${APP_API_KEY:default-secret-key}`). Quedan exentos
  `/actuator`, `/swagger-ui` y `/v3/api-docs`. **Rompe compatibilidad** con cualquier
  cliente que hoy llame a compras sin el header.
- Esquema de seguridad `api-key` en la definición OpenAPI, para enviar el header desde
  Swagger UI.
- Diagramas `docs/diagrams/arquitectura-general.mmd` y
  `docs/diagrams/flujo-consulta-proveedor.mmd`, que el flujo de documentación inyecta en
  el sitio.
- Flujos de CI/CD: `maven.yml` (build y análisis en SonarCloud, y publicación de la imagen
  JVM sobre `main`), `deploy-develop.yml` y `deploy-staging.yml` (verificación,
  publicación de imagen y despliegue por runner propio), y `generate-docs.yml` (sitio de
  documentación con Mermaid en GitHub Pages, más la wiki como portal).
- Propiedades de SonarCloud (`sonar.organization`, `sonar.projectKey`, `sonar.host.url`)
  en el `pom.xml`.

### Cambiado

- Los slices `articulo` y `proveedor` viven bajo `tesoreria.compras.slice.*`; el contrato
  de `proveedor` no cambia.
- La definición OpenAPI publica la versión del servicio (`0.3.0`) en lugar de la inicial
  `0.1.0`.
- El workflow `build.yml` se reemplaza por `maven.yml` y los `deploy-*.yml`; ya no se corre
  `REQUIRE_DOCKER`, porque no quedan pruebas de integración propias.
- Dependencias actualizadas: Spring Boot `4.1.0` → `4.1.1`, Spring Cloud `2025.1.2` →
  `2025.1.3` y springdoc `3.0.3` → `3.1.1`.
- Puerto por defecto `8203` → `8096`, alineado con el compose compartido.
- Las fixtures de `proveedor` usan un nombre sintético en lugar del nombre real del
  proveedor 8.

### Eliminado

- Dependencias `spring-boot-starter-data-jpa`, `mysql-connector-j`,
  `spring-boot-testcontainers`, `spring-boot-data-jpa-test` y `testcontainers-mysql`, y la
  configuración `spring.datasource`/`spring.jpa` de `bootstrap.yml`: el servicio deja de
  tener persistencia propia.
- `spring-boot-starter-validation` y `testcontainers-junit-jupiter`, sin uso, y el
  `maven-failsafe-plugin`, que ya no tiene pruebas de integración que ejecutar.
- `docker-compose.yml` propio y las pruebas de integración contra MySQL de Testcontainers,
  junto con su DDL.
- El diagrama hexagonal del agregado retirado.

## [0.1.0] - 2026-08-05

Primer esqueleto ejecutable del servicio.

### Agregado

- Documentación en `specs/`: roadmap y stack técnico, más la carpeta del primer feature
  (esqueleto del servicio) con sus requerimientos, plan y validación.
- Proyecto Maven en Java 25 con Spring Boot, Consul, OpenFeign, Actuator, OpenAPI y
  JaCoCo con cobertura mínima del 80% que falla el build.
- `Dockerfile` multi-stage y `docker-compose.yml` propio para ejecutar el servicio en la
  red compartida.
- Endpoint `GET /api/tesoreria/compras/ping/{proveedorId}`, que consume proveedores de
  core por Feign y Consul.
- Traducción de errores de Feign dentro del adapter: proveedor inexistente responde `404`
  y core no disponible responde `503`.
