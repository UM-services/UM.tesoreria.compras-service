# Plan — Adjudicación por monto y bandeja de respuestas

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-11
Requirements: [requirements.md](requirements.md) · Decisiones: `PLANIFICACION_COMPRAS.md` §15.

---

## Pieza A — Bandeja "Cotizaciones pedidas" (implementada)

1. **core · slice `compraCotizacion`**
   - `CompraCotizacionService.respuestasPendientes(ejercicioId)`: parte del listado por ejercicio
     (`findByCriteria`), retiene los procesos con ≥1 documento `ENVIADA` y aplana a
     `CompraCotizacionProveedorPendiente` (proceso + documento + ítems del proceso).
   - `GET /api/tesoreria/core/compraCotizacion/proveedores-pendientes?ejercicioId=` →
     `CompraCotizacionProveedorPendienteResponse`. Aditivo, **sin DDL**.
2. **tesoreria-compras (fachada)**
   - `CotizacionProcesoGateway.respuestasPendientes` + Feign
     `GET /api/tesoreria/core/compraCotizacion/proveedores-pendientes`.
   - `GET /api/tesoreria/compras/cotizacion/proveedores-pendientes`, permiso
     `compras.cotizaciones.gestionar` (PEP declarativo del controller). El ejercicio en curso se
     resuelve con `EjercicioActualGateway`.
3. **tesoreria-frontend · `feature-pedido-compra`**
   - Pantalla **Cotizaciones pedidas** (`/cotizacion/pedidas`): tabla de documentos pendientes y
     carga de la respuesta (reutiliza `registrarRespuesta`) + "No cotiza" + enlace al proceso.
   - Ruta e ítem de menú con `permisoGuard`/`ShellMenuItem.permiso = 'compras.cotizaciones.gestionar'`.

## Pieza B — Adjudicación por monto (implementada)

1. **core · DDL `V411`**: tabla `compra_adjudicacion` + ensanche de `compra_cotizacion.estado` a
   `VARCHAR(100)`; seed de la clave `compras.adjudicacion.autorizar`.
2. **core · slice `compraCotizacion`**: `AdjudicarCompraCotizacionUseCase` registra la decisión
   (`ADJUDICADA` / `DEVUELTA_A_COMPRAS` / `NO_ADJUDICADA`) con proveedor, monto, autoridad y
   fundamento; transición de estados (`CompraCotizacion.resolverAdjudicacion`) y del pedido a
   `ADJUDICADO` (`CompraPedidoService.marcarAdjudicado`). `GET /{id}/adjudicacion` devuelve el acto.
3. **tesoreria-compras (fachada)**: `AdjudicacionCotizacionController`
   (`/cotizacion/adjudicacion`) con `GET /bandeja` · `/limite`, `POST /{id}/adjudicar|devolver|no-adjudicar`
   y `GET /{id}`, permiso `compras.adjudicacion.autorizar`, validación del límite **fail-closed**
   (`403 LIMITE_AUTORIZACION_EXCEDIDO`) contra el total de la cotización recomendada (reutiliza
   `AutoridadGateway` de §11).
4. **tesoreria-frontend**: pantalla *Adjudicación* (`/cotizacion/adjudicacion`) con la cotización
   recomendada, el total y el límite visibles, y las acciones con fundamento.
