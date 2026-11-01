# Plan — Recepción y conformidad (fachada)

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-14
Requirements: [requirements.md](requirements.md) · Decisiones: `PLANIFICACION_COMPRAS.md` §18.

---

## 1. Modelo de dominio de la fachada (records, sin frameworks)

- `Recepcion` / `RecepcionItem`: la entrega y su detalle (cantidad recibida y resultado por ítem).
- `RecepcionPendiente` / `RecepcionPendienteItem`: la OC con su **saldo derivado** (contratado,
  recibido, saldo, `completo`) y sus entregas.
- `RecepcionSolicitud` / `RecepcionItemSolicitud`: datos del alta (el número y el resultado general
  los resuelve core).
- `RecepcionFiltro`: ejercicio, OC, resultado y rango de fechas.

## 2. Puerto y servicio

- `RecepcionGateway` (salida): `pendientes`, `listar`, `getById`, `porFormalizacion`, `registrar`,
  `cerrar`.
- `RecepcionService` (`@Service`): fachada que resuelve el **ejercicio en curso** con
  `EjercicioActualGateway` si no llega y no se discrimina por OC. Expone
  `PERMISO_RECEPCIONAR = "compras.recepcionar"`.

## 3. Adaptador Feign

- `CoreCompraRecepcionFeignClient` (`contextId = "coreCompraRecepcionClient"`) →
  `/api/tesoreria/core/compraRecepcion` (`/pendientes`, `/`, `/search`, `/{id}`,
  `/por-formalizacion/{id}`, `POST /` y `/formalizacion/{id}/cerrar`).
- `CoreCompraRecepcionFeignClientAdapter`: mapea los DTOs de core al dominio de la fachada y traduce
  `FeignException.NotFound` → `RecepcionNotFoundException`, `BadRequest` → `RecepcionInvalidaException`,
  `Conflict` → `RecepcionConflictoException` y el resto → `PedidoCompraSourceUnavailableException`
  (`503`). Para `porFormalizacion`, el `404` de core se traduce a `Optional.empty()`.

## 4. Web

- `RecepcionController` (`/api/tesoreria/compras/recepcion`) con `@RequierePermiso(...)` a nivel de
  clase y el actor por `X-User-Id`: `GET /pendientes`, `GET`, `POST /search`, `GET /{id}`,
  `GET /por-formalizacion/{id}`, `POST` y `POST /formalizacion/{id}/cerrar` (`204`).
- `RecepcionDtoMapper` (DTOs ↔ dominio) y DTOs `RecepcionRequest`/`Response`,
  `RecepcionItemRequest`/`Response`, `RecepcionPendienteResponse`/`ItemResponse`,
  `RecepcionCriteriaRequest` y `CerrarRecepcionRequest`.
- `PedidoCompraExceptionHandler`: tres handlers nuevos (`404`/`400`/`409`).

## 5. Frontend (`feature-pedido-compra`, app `compras`)

- `RecepcionService` (`/api/tesoreria/compras/recepcion`) y `models/recepcion.models.ts`.
- Pantalla **Recepción** (`/recepcion`): bandeja de OCs con saldo pendiente y **alta** de la entrega
  inline (fecha, remito, patrimonio, observaciones y por ítem: cantidad recibida, resultado y
  observación), con **tipo calculado** (Total cuando cubre todos los saldos) y validación de cliente
  (no exceder el saldo, al menos una cantidad mayor a cero); debajo, las recepciones registradas.
- Pantalla **detalle** (`/recepcion/:compraFormalizacionId`): saldo por ítem, entregas registradas y
  **cierre manual** con motivo.
- Rutas `recepcionRoutes` con `permisoGuard('compras.recepcionar')`, montaje `loadChildren` en
  `apps/compras` y menú **«8. Recepción»** con el mismo permiso.

## 6. Documentación

- `specs/2026-10-14-recepcion/` (este expediente) y fila nueva en `specs/roadmap.md`.
- `CHANGELOG.md` (sección `[Unreleased]`).
