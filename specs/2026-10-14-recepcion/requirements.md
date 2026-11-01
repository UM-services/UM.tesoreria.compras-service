# Requirements — Recepción y conformidad (fachada)

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-14 · Estado: **✅ terminado**
Registro de decisiones: `PLANIFICACION_COMPRAS.md` (raíz) **§18**.
Decisiones del circuito: **D-REC-1..13** (§18.8).

Global: [stack y convenciones](../tech-stack.md).

---

## Objetivo

Cerrar el circuito del proceso de compras: registrar la **entrega del proveedor** contra una Orden
de Compra (total o parcial), con el resultado por ítem y general, el remito, la intervención de
Patrimonio y las observaciones, dejando el pedido en `RECEPCION_PARCIAL`/`RECEPCIONADO`. La fachada
**no persiste**: compone contra `core` por Feign y exige el permiso declarativamente.

## Alcance

| Pieza | Contenido | Estado |
|---|---|---|
| Fachada gateada | Controller `/api/tesoreria/compras/recepcion` con `@RequierePermiso("compras.recepcionar")`, puerto `RecepcionGateway` (Feign, contexto `coreCompraRecepcionClient`) y traducción de errores de core a `404`/`400`/`409`/`503`. | ✅ |
| Bandeja y consulta | `GET /pendientes` (OCs del ejercicio con saldo pendiente, ejercicio en curso por defecto), `GET` y `POST /search` (filtros: ejercicio, OC, resultado, fechas), `GET /{id}` y `GET /por-formalizacion/{compraFormalizacionId}` (recepciones y saldos). | ✅ |
| Registro | `POST` (entrega total o parcial) y `POST /formalizacion/{id}/cerrar` (cierre manual con motivo obligatorio). | ✅ |

Fuera de alcance (en core o etapas posteriores): persistencia (vive en core), envío real de correo,
PDF/acta de recepción, alta patrimonial del bien y el Contrato (D-FOR-5).

## REQ

- **REQ-REC-1**: `GET /api/tesoreria/compras/recepcion/pendientes` lista las Órdenes de Compra con
  recepción pendiente del ejercicio indicado (o del ejercicio en curso), con permiso
  `compras.recepcionar`.
- **REQ-REC-2**: `POST /api/tesoreria/compras/recepcion` registra una entrega (cabecera + ítems) y
  devuelve la recepción con su número `RC-AAAA-NNNNNN`; el actor llega por el header `X-User-Id`.
- **REQ-REC-3**: `GET /api/tesoreria/compras/recepcion/por-formalizacion/{compraFormalizacionId}`
  devuelve las recepciones registradas y el saldo por ítem de la Orden de Compra.
- **REQ-REC-4**: `POST /api/tesoreria/compras/recepcion/formalizacion/{compraFormalizacionId}/cerrar`
  cierra manualmente la recepción con motivo obligatorio (el motivo se asienta en el historial del
  pedido en core).
- **REQ-REC-5**: los errores de core se traducen a `404` (`RecepcionNotFoundException`), `400`
  (`RecepcionInvalidaException`), `409` (`RecepcionConflictoException`) y `503`
  (`PedidoCompraSourceUnavailableException`).
- **REQ-REC-6**: permiso nuevo `compras.recepcionar` (registrado en el catálogo de core y cableado
  en el código nuevo: `@RequierePermiso` en la fachada, `permisoGuard`/`ShellMenuItem.permiso`/
  `*uiPermiso` en el frontend). Ningún endpoint existente cambia.

## Referencias

- Backend (core): slice `compraRecepcion` + estados `RECEPCION_PARCIAL`/`RECEPCIONADO` del pedido y
  `docs/sql/V413__compras_recepcion.sql` (tablas `compra_recepcion`, `compra_recepcion_item` y
  `compra_recepcion_secuencia`).
- Frontend: pantallas `/recepcion` y `/recepcion/:compraFormalizacionId` en
  `feature-pedido-compra` (app `compras`).
