# Changelog

Formato: [Keep a Changelog](https://keepachangelog.com/es/1.1.0/) ·
Versionado: [SemVer](https://semver.org/lang/es/)

La versión vive únicamente en el `<version>` del `pom.xml`.

---

## [Unreleased]

## [0.11.2] - 2026-10-23

### Cambiado

- chore(deps): Spring Boot `4.1.1` → `4.1.2` (vía `spring-boot-starter-parent`), Spring Cloud
  `2025.1.3` → `2025.1.4`, Lombok `1.18.38` → `1.18.48` y `jacoco-maven-plugin`
  `0.8.13` → `0.8.15`. Sin cambios en el contrato del servicio; las puertas de cobertura
  (80 % línea, 75 % rama, 70 % línea por clase) se mantienen.
- `specs/tech-stack.md` se alinea con el `pom.xml` vigente: Spring Boot `4.1.2`, Spring Cloud
  `2025.1.4`, Lombok `1.18.48` y Kotlin `2.4.21`.
- La definición OpenAPI publica la versión del servicio (`0.11.2`).

### Corregido

- fix(consul): la resolución de instancias por parte de Feign/LoadBalancer pasa a pedir sólo
  instancias con los checks en `passing` (`spring.cloud.consul.discovery.query-passing: true`).
  Complementa el registro contra el readiness y la poda por criticidad de `0.11.1`: una
  instancia degradada ya no se resuelve mientras Consul todavía no la marcó crítica. Sin
  cambios en el contrato del servicio.

## [0.11.1] - 2026-10-21

### Cambiado

- chore(consul): el chequeo que Consul usa para el registro del servicio pasa del health
  agregado (`/actuator/health`) al **readiness** (`/actuator/health/readiness`), que no
  incluye las dependencias externas; además, el servicio se poda del catálogo si el chequeo
  queda crítico durante 5 minutos (`health-check-critical-timeout: 5m`). Evita que una
  degradación de `core-service` marque la instancia como crítica en Consul y la saque de
  rotación. Los *probes* ya estaban habilitados (`management.endpoint.health.probes.enabled`).
  Sin cambios en el contrato del servicio.
- La definición OpenAPI publica la versión del servicio (`0.11.1`).

## [0.11.0] - 2026-10-20

### Cambiado

- feat(pedidoCompra): `POST /api/tesoreria/compras/pedido/bandeja` pasa de devolver un array
  a `{ pedidos, dependenciaIds, sinDependencias }`. La fachada conserva las dependencias
  habilitadas del autorizante (el caso de uso ahora devuelve `BandejaEnvio`) y expone
  `sinDependencias`, para que el cliente explique una bandeja vacía por falta de asignación
  en lugar de mostrarla como "sin pedidos". **Cambio de contrato**: requiere desplegar en
  conjunto con el frontend de compras.
- La definición OpenAPI publica la versión del servicio (`0.11.0`).

## [0.10.1] - 2026-10-19

### Cambiado

- ci(deploy-staging): el job `deploy` corre en matriz contra los entornos `staging` y
  `staging-new` (`fail-fast: false`), cada uno en el runner self-hosted con la etiqueta de
  su entorno y con la misma imagen publicada (`${{ github.sha }}`) y el mismo script de
  despliegue. El fallo de un entorno no cancela el otro; requiere que existan el entorno
  `staging-new` en GitHub y su runner con esa etiqueta. Sin cambios en el contrato del
  servicio.
- La definición OpenAPI publica la versión del servicio (`0.10.1`).

## [0.10.0] - 2026-10-16

### Agregado

- feat(pedidoCompra): **fachada de la Recepción y conformidad** (etapa 9 — `PLANIFICACION_COMPRAS.md` §18). Nuevo controller `/api/tesoreria/compras/recepcion` con permiso `compras.recepcionar` (PEP declarativo) y puerto `RecepcionGateway` (Feign a core, contexto `coreCompraRecepcionClient`):
  - `GET /pendientes` — Órdenes de Compra con recepción pendiente (ejercicio en curso por defecto), con el **saldo derivado** por ítem y las entregas ya registradas.
  - `GET` y `POST /search` — recepciones registradas con filtros (ejercicio, OC, resultado, fechas).
  - `GET /{id}` y `GET /por-formalizacion/{compraFormalizacionId}` — detalle de la entrega y saldos de la OC.
  - `POST` — registra una entrega (total o parcial) contra la OC; el actor llega por `X-User-Id`.
  - `POST /formalizacion/{compraFormalizacionId}/cerrar` — cierre manual con motivo obligatorio (`204`).
  Traducción de errores de core a `404`/`400`/`409` (`RecepcionNotFoundException`/`Invalida`/`Conflicto`) y `503` si core no responde. El mapeo de `porFormalizacion` convierte el `404` de core en `Optional.empty()`.

### Cambiado

- La definición OpenAPI publica la versión del servicio (`0.10.0`).

### Eliminado

- Se elimina el archivo `VERSION`: la versión del servicio queda en un único lugar, el
  `<version>` del `pom.xml`. Sin cambios funcionales.

## [0.9.0] - 2026-10-12

### Agregado

- feat(pedidoCompra): **fachada de la Orden de Compra** (etapa 8 — `PLANIFICACION_COMPRAS.md` §17). Nuevo controller `/api/tesoreria/compras/formalizacion` con permiso `compras.formalizar` (PEP declarativo) y puerto `FormalizacionGateway` (Feign a core, contexto `coreCompraFormalizacionClient`): `GET /pendientes` (adjudicaciones sin OC, con el ejercicio en curso por defecto), `GET` / `POST /search` (listado con filtros), `GET /{id}`, `GET /por-cotizacion/{compraCotizacionId}` y `POST` (emite la OC; el actor llega por `X-User-Id`). Traducción de errores de core a `404`/`400`/`409` (`FormalizacionNotFoundException`/`Invalida`/`Conflicto`) y `503` si core no responde. El instrumento snapshotea proveedor, monto, condiciones e ítems desde la adjudicación; **sin** límite por monto (la cuantía se decidió en la adjudicación).

### Cambiado

- La definición OpenAPI publica la versión del servicio (`0.9.0`).

## [0.8.0] - 2026-10-11

### Agregado

- feat(pedidoCompra): **Configuración de tramos de cotizaciones mínimas por monto** (fachada gateada sobre la configuración de core). Endpoints nuevos bajo `/api/tesoreria/compras/cotizacion/tramos`, con permiso `compras.cotizaciones.configurar` vía `@RequierePermiso` (PEP declarativo del servicio):
  - `GET` — lista los tramos (tope en múltiplos de la referencia; `abierto` = tramo sin tope).
  - `POST` — crea un tramo.
  - `PUT /{cotizacionTramoId}` — edita un tramo.
  - `DELETE /{cotizacionTramoId}` — elimina un tramo.
  Nuevo puerto `CotizacionGateway` (Feign) → `/api/tesoreria/core/compraCotizacionTramo`, con traducción de errores de core a `400/409/404` (`CotizacionTramoInvalidaException`, `CotizacionTramoConflictoException`, `CotizacionTramoNotFoundException`) o `503` si core no responde. La resolución del mínimo por pedido y la gestión de cotizaciones son etapas posteriores.
- feat(pedidoCompra): el `PedidoCompraResponse` de la fachada expone `cantidadCotizacionesMinima` (espeja la columna nueva de core). No se completa todavía.
- feat(pedidoCompra): **Mínimo de cotizaciones por pedido** (etapa 6, fase A — `PLANIFICACION_COMPRAS.md` §14). Nuevo endpoint `GET /api/tesoreria/compras/pedido/{id}/cotizaciones-minimas` (permiso `compras.estimar`) que devuelve el snapshot del pedido o el cálculo vigente. Nuevo método `CotizacionGateway.resolverMinimo` (Feign `GET /api/tesoreria/core/compraCotizacionTramo/minimo`) que traduce el `409` de core a `CotizacionesNoConfiguradasException` → `409` con `codigo: COTIZACIONES_NO_CONFIGURADAS` (o `503` si core no responde). La emisión del proceso de cotización y la carga de respuestas son la fase siguiente.
- feat(pedidoCompra): **Procesos de cotización** (etapa 6, fase B). Nuevos endpoints bajo `/api/tesoreria/compras/cotizacion`, con permiso `compras.cotizaciones.gestionar` (PEP `@RequierePermiso`): `GET /pendientes` (ítems de pedidos autorizados; el ejercicio en curso se resuelve por defecto), `GET /` y `POST /search`, `GET /{id}`, `POST /`, `PUT /{id}`, `POST /{id}/enviar` y `POST /{id}/anular`. Nuevo puerto `CotizacionProcesoGateway` (Feign a core → `/api/tesoreria/core/compraCotizacion`) con traducción de errores a `400/409/404` o `503`. La carga de respuestas es la fase 6C.
- feat(pedidoCompra): **Respuestas, comparación y recomendación** (etapa 6, fase C). Endpoints nuevos (mismo permiso): `POST /cotizacion/{id}/proveedores/{cpId}/respuesta`, `POST /cotizacion/{id}/proveedores/{cpId}/no-cotiza`, `GET /cotizacion/{id}/comparacion`, `POST /cotizacion/{id}/recomendar` y `POST /cotizacion/{id}/cerrar`. La respuesta incluye el precio por ítem del proceso; al recomendar, los pedidos cubiertos pasan a `PENDIENTE_ADJUDICACION`.
- feat(pedidoCompra): **Bandeja "Cotizaciones pedidas"** (etapa 7, pieza A — `PLANIFICACION_COMPRAS.md` §15). Nuevo `GET /api/tesoreria/compras/cotizacion/proveedores-pendientes` (permiso `compras.cotizaciones.gestionar`, `@RequierePermiso`): documentos de proveedor en `ENVIADA` de todos los procesos, para cargar sus valores sin entrar proceso por proceso. El ejercicio en curso se resuelve con `EjercicioActualGateway` si no llega. Nuevo método `CotizacionProcesoGateway.respuestasPendientes` (Feign a core). Aditivo; la carga reutiliza el endpoint de respuestas ya existente.
- feat(pedidoCompra): **Adjudicación por monto** (etapa 7, pieza B). Controlador nuevo `AdjudicacionCotizacionController` bajo `/api/tesoreria/compras/cotizacion/adjudicacion`, con permiso `compras.adjudicacion.autorizar` (PEP `@RequierePermiso`): `GET /bandeja` (procesos `RECOMENDADA`), `GET /limite` y `/limite/{ejercicioId}` (reutiliza `AutoridadGateway` de §11), `POST /{id}/adjudicar` (oferta elegida + fundamento; si no se indica, la recomendada), `POST /{id}/devolver`, `POST /{id}/no-adjudicar` y `GET /{id}`. El límite por monto se valida **fail-closed** contra el **total de la cotización recomendada** (`403 LIMITE_AUTORIZACION_EXCEDIDO`). Nuevos métodos en `CotizacionProcesoGateway` (Feign a core).

### Cambiado

- La definición OpenAPI publica la versión del servicio (`0.8.0`).

## [0.7.0] - 2026-10-11

### Agregado

- feat(pedidoCompra): `GET /api/tesoreria/compras/pedido/presupuesto/limite` (sin ejercicio) resuelve el **ejercicio en curso** (Feign a core `GET /ejercicio/last`) y devuelve el límite efectivo del usuario; así la pantalla de autorización de presupuesto lo muestra aunque la bandeja esté vacía. Nuevo puerto `EjercicioActualGateway` + `CoreEjercicioFeignClient`/`CoreEjercicioFeignClientAdapter`.

### Corregido

- fix(pedidoCompra): el límite de autorización ya no depende de que existan pedidos pendientes; antes, con la bandeja vacía, no se consultaba y la UI mostraba "sin perfil" aunque el usuario sí tuviera autoridad.

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
