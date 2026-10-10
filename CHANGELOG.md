# Changelog

Formato: [Keep a Changelog](https://keepachangelog.com/es/1.1.0/) ·
Versionado: [SemVer](https://semver.org/lang/es/)

La versión vive en [`VERSION`](VERSION) y en el `<version>` del `pom.xml`; mantener los dos
sincronizados: `VERSION` es lo que lee el flujo de release, `pom.xml` lo que lee Maven.

---

## [0.6.0] - 2026-10-10

### Cambiado

- refactor(compras): se eliminan los `@RequestParam` de la fachada y de los clientes Feign hacia core, alineados al cambio de contrato de core (path para requeridos, cuerpo de `POST` para opcionales/paginación): `POST /api/tesoreria/compras/articulo/tipo/{tipo}/page` (cuerpo `PageRequest`), `POST /api/tesoreria/compras/proveedor/page` (cuerpo `PageRequest`), `POST /api/tesoreria/compras/pedido/bandeja|consulta|revision` (cuerpo con los filtros) y `GET /api/tesoreria/compras/pedido/presupuesto/limite/{ejercicioId}`. Los Feign `CoreArticulo`, `CoreProveedor`, `CoreCompraPedido` (enviar/listar) y `CoreCompraAutoridad` (límite) se actualizan en lockstep; nuevo `tesoreria.compras.model.PageRequest`, `BandejaPedidoRequest`, `ConsultaPedidoRequest` y `CoreEnviarCompraPedidoRequest`.

### Agregado

- Etapa de **autorización previa por monto** del pedido de compra (fachada gateada sobre la
  autoridad por monto resuelta en core):
  - `POST /api/tesoreria/compras/pedido/revision` (permiso `compras.estimar`): bandeja de revisión
    del dpto. de compras (por defecto `EN_REVISION_COMPRAS`).
  - `POST /{id}/estimar` (`compras.estimar`): carga/confirma el valor estimado del pedido enviado.
  - `GET /presupuesto/bandeja` y `GET /presupuesto/limite/{ejercicioId}` (`compras.presupuesto.autorizar`):
    bandeja de la autoridad por monto y su límite efectivo (`multiplico × referencia`).
  - `POST /{id}/autorizar-presupuesto` y `POST /{id}/rechazar-presupuesto` (`compras.presupuesto.autorizar`):
    decisión de la autoridad. El autorizar es **fail-closed**: exige `montoEstimado ≤ límite`
    (o perfil ilimitado); al excederse responde `403` `ProblemDetail` con
    `codigo: LIMITE_AUTORIZACION_EXCEDIDO`, `monto` y `limite`.
  - Puerto `AutoridadGateway` (Feign) → `GET /api/tesoreria/core/compraAutoridadUsuario/limite/{usuarioId}/{ejercicioId}`.
- Fachada gateada de las pantallas **Gastos** y **Proveedores** bajo
  `/api/tesoreria/compras/...`, que consume `core-service` por Feign para que el
  `compras-client` no le pegue directo al core (que no puede llevar gating por el legacy).
- PEP propio (`configuration/security`): anotación `@RequierePermiso`, interceptor que
  evalúa la clave contra el bundle efectivo de core (`X-User-Id` transitorio) y responde
  `403`/`401` como `ProblemDetail`. Activo por defecto (`APP_PERMISSIONS_ENFORCE=true`) y
  acotado a endpoints anotados.
- Slice `articulo`: `POST /tipo/{tipo}/page`, `GET /new`, `POST /`, `PUT /{id}` y
  `DELETE /{id}` (más `GET /{id}` y `POST /search` ya existentes) con `compras.gastos*`.
- Slice `proveedor`: `POST /page`, `POST /search`, `GET /cuit/{cuit}`, `GET /{id}`,
  `POST /`, `PUT /{id}` y `DELETE /{id}` con `compras.proveedores*`.
- Slices `ubicacion` (`GET /`) y `ubicacionArticulo` (`GET /articulo/{id}`,
  `POST /`) con `compras.gastos`/`compras.gastos.imputar`.
- Slice `sheet`: `GET /generateProveedores` con `compras.proveedores.descargar`.
- `PaginatedResponse` propio que preserva el shape JSON de core.

### Cambiado

- El `compras-client` (`feature-gastos`, `feature-proveedores`) pasa a llamar a
  `/api/tesoreria/compras/**`. Los buscadores compartidos siguen usando core.
- El diagrama `docs/diagrams/arquitectura-general.mmd` se actualiza con los slices de la
  fachada, el PEP y sus adapters Feign.
- La definición OpenAPI publica la versión del servicio (`0.6.0`).


## [0.5.0] - 2026-10-08

### Agregado

- Slice `pedidoCompra`: circuito de decisión y consulta del pedido. Nuevos endpoints bajo
  `/api/tesoreria/compras/pedido`:
  - `GET /bandeja`: pedidos de las dependencias habilitadas del autorizante, con filtro
    opcional por `estado`.
  - `GET /consulta`: consulta global con filtros por `estado`, `solicitanteId`,
    `dependenciaId` y rango `fechaDesde`/`fechaHasta`.
  - `POST /{compraPedidoId}/aprobar`, `POST /{compraPedidoId}/rechazar` (con `motivo`) y
    `POST /{compraPedidoId}/descartar` (con `motivo`).
  - `GET /{compraPedidoId}/historial`: línea de tiempo de estados expuesta por core.
- Permisos `compras.enviar_pedido` (bandeja y decisión del autorizante) y
  `compras.consultar_pedidos` (consulta global), verificados contra el bundle efectivo de
  core (`GET /api/tesoreria/core/permisoEfectivo/usuario/{userId}`); `403` si falta.
- Acceso acotado por identidad: el autorizante sólo decide sobre pedidos de las
  dependencias que tiene habilitadas (`GET /api/tesoreria/core/compraPedidoAutorizante/dependencias/{autorizanteId}`),
  el solicitante sólo opera sobre sus propios pedidos, y la lectura se habilita por
  permiso, por dependencia autorizada o por pertenencia.
- Enriquecimiento de los listados con `dependenciaNombre` y `solicitanteNombre`, resueltos
  contra core y degradando a `null` si la resolución falla, para no romper el listado.
- Clientes Feign contra core: `POST /compraPedido/search`,
  `POST /compraPedido/{id}/aprobar|rechazar|descartar`,
  `GET /compraPedidoHistorial/{compraPedidoId}` y
  `GET /compraPedidoAutorizante/dependencias/{autorizanteId}`.

### Cambiado

- `POST /{compraPedidoId}/enviar` (y `enviar=true` en alta/edición) propaga el `usuarioId`
  a core y exige que el usuario sea el solicitante del pedido.
- `GET /{compraPedidoId}` y `GET /{compraPedidoId}/historial` ahora exigen `X-User-Id` y
  validan el acceso; responden `403` si el usuario no tiene permiso ni pertenencia.
  **Rompe compatibilidad** con clientes que consultaban el pedido sin identidad.
- `GET /api/tesoreria/compras/pedido` (listado por solicitante) ahora exige el permiso
  `compras.iniciar_pedido`, además de la identidad.
- `PedidoCompraResponse` expone `fechaEnvio`, `rechazoMotivo`, `descartadoMotivo`,
  `dependenciaNombre` y `solicitanteNombre`.
- La definición OpenAPI publica la versión del servicio (`0.5.0`).

---

## [0.4.0] - 2026-10-07

### Agregado

- Slice `pedidoCompra`: fachada del circuito "iniciar pedido de compra". Compone el contexto
  (solicitante + dependencia desde `core-service`), el alta/edición del borrador, el envío
  (que asigna `PC-AAAA-NNNNNN` en core) y la consulta. Endpoints canónicos
  `/api/tesoreria/compras/pedido...`; identidad por header `X-User-Id`.
- Verificación del permiso `compras.iniciar_pedido` contra el bundle efectivo de core
  (`GET /api/tesoreria/core/permisoEfectivo/usuario/{userId}`); `403` si falta.

### Cambiado

- La definición OpenAPI publica la versión del servicio (`0.4.0`).

---

## [0.3.1] - 2026-10-06

### Cambiado

- El artefacto Maven y el JAR pasan de `tesoreria-compras-service` a
  `um.tesoreria.compras-service`: el `<finalName>` del `pom.xml` y el `COPY`/`ENTRYPOINT`
  del `Dockerfile` quedan alineados con el nombre del repositorio, de la definición OpenAPI
  y de SonarCloud.
- La definición OpenAPI publica la versión del servicio (`0.3.1`).

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
