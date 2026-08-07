# Changelog

Formato: [Keep a Changelog](https://keepachangelog.com/es/1.1.0/) ·
Versionado: [SemVer](https://semver.org/lang/es/)

La versión vive en [`VERSION`](VERSION) y en el `<version>` del `pom.xml`; mantener los dos
sincronizados: `VERSION` es lo que lee el flujo de release, `pom.xml` lo que lee Maven.

---

## [0.2.0] - 2026-08-07

Feature 2 — orden de compra. La versión refleja el código entregado; el cierre formal de
la feature todavía depende de que el DBA consensúe el esquema (T1.3).

### Agregado

- Agregado `OrdenCompra` con ítems, imputación por ítem, total calculado y máquina de
  estados explícita, con persistencia propia bajo `ddl-auto: none`.
- Endpoints bajo `/api/tesoreria/compras/ordenCompra`: alta, consulta por id y por número,
  listado con filtros opcionales y comandos de transición de estado.
- Numeración pública `OC-AAAA-NNNNNN` (decisión B11), reservada de forma atómica con
  `LAST_INSERT_ID` dentro de la transacción del alta.
- DDL solicitado al DBA en `src/test/resources/db/compra-orden-ddl.sql`, único lugar donde
  vive el SQL y el mismo archivo que levantan las pruebas de integración.
- Pruebas de integración contra MySQL real con Testcontainers: numeración concurrente,
  identidad de ítems entre estados, rango inclusivo de fechas, paginado y arranque de la
  aplicación completa.
- Wrapper `mvnw` versionado y CI en GitHub Actions que corre `./mvnw verify` con
  `REQUIRE_DOCKER=true`.

### Cambiado

- El listado es siempre paginado: 50 por página por defecto, tope de 200. La respuesta pasó
  de ser un arreglo a un objeto con `contenido`, `pagina`, `tamano`, `totalElementos` y
  `totalPaginas`. **Rompe compatibilidad** con cualquier consumidor del arreglo.
- `OrdenCompraItemResponse` expone `id`, para poder referenciar un renglón de forma estable.
- La puerta de JaCoCo suma un mínimo de rama del 75 % y un piso por clase del 70 %, además
  del 80 % de línea que ya existía.

### Corregido

- El alta reservaba el correlativo fuera de la transacción del insert: un guardado fallido
  quemaba un número y dejaba huecos en la serie.
- Un cambio de estado borraba y reinsertaba todos los ítems de la orden con identificadores
  nuevos, por construir siempre entidades sin `id` sobre una colección con `orphanRemoval`.
- El manejador de errores capturaba `IllegalArgumentException` e `IllegalStateException` de
  toda la aplicación, convertía errores de servidor en `400` y filtraba el mensaje interno.
- El listado no tenía tope y una consulta sin filtros traía la tabla entera a memoria.
- Los patrones de `<excludes>` de JaCoCo no terminaban en `.class` y por eso no excluían
  nada.

## [0.1.0] - 2026-08-05

Primer esqueleto ejecutable del servicio.

### Agregado

- Documentación en `specs/`: misión, roadmap y stack técnico, más la carpeta del primer
  feature (esqueleto del servicio) con sus requerimientos, plan y validación.
- Proyecto Maven en Java 25 con Spring Boot, Consul, OpenFeign, Actuator, OpenAPI y
  JaCoCo con cobertura mínima del 80% que falla el build.
- `Dockerfile` multi-stage y `docker-compose.yml` propio para ejecutar el servicio en la
  red compartida.
- Endpoint `GET /api/tesoreria/compras/ping/{proveedorId}`, que consume proveedores de
  core por Feign y Consul.
- Traducción de errores de Feign dentro del adapter: proveedor inexistente responde `404`
  y core no disponible responde `503`.
