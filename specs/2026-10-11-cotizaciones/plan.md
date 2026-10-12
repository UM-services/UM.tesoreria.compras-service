# Plan — Cotizaciones (pedidos de cotización)

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-11
Requirements: [requirements.md](requirements.md) · Decisiones: `PLANIFICACION_COMPRAS.md` §14.10.

---

## Fase 6A — Aplicación del mínimo (implementada)

1. **core · slice `compraCotizacionTramo`**
   - `ResolutorMinimoCotizaciones` (puro): primer tramo activo por tope ascendente cuyo
     `tope (múltiplo × referencia)` alcance el monto; el tramo abierto cubre el resto.
   - `CompraCotizacionTramoService.resolverMinimo(ejercicioId, monto)` compone los tramos con la
     referencia del ejercicio (`CompraReferenciaService`, composición cross-slice ya pautada) y
     devuelve `Optional<Integer>` (vacío = no configurado).
   - `GET /api/tesoreria/core/compraCotizacionTramo/minimo?ejercicioId=&monto=` → `409` con
     `COTIZACIONES_NO_CONFIGURADAS` si no se resuelve.
2. **core · slice `compraPedido`**
   - `CompraPedido.estimar(monto, fuente, cantidadMinima)` persiste el snapshot.
   - `CompraPedidoService.estimar` resuelve el mínimo (best-effort) y lo pasa.
   - `GET /api/tesoreria/core/compraPedido/{id}/cotizaciones-minimas` (snapshot o cálculo).
3. **tesoreria-compras (fachada)**
   - `CotizacionGateway.resolverMinimo` + Feign `GET .../minimo`.
   - `CotizacionesNoConfiguradasException` (`409`, `codigo: COTIZACIONES_NO_CONFIGURADAS`).
   - `GET /api/tesoreria/compras/pedido/{id}/cotizaciones-minimas` con permiso `compras.estimar`.
4. **tesoreria-frontend · `feature-pedido-compra`**
   - `PedidoCompra.cantidadCotizacionesMinima` en el modelo.
   - "Cotizaciones mínimas requeridas: **N**" en `pedido-revision` y `pedido-detalle`.

## Fase 6B — Emisión del proceso (implementada)

- **core · slice `compraCotizacion`**: cabecera `compra_cotizacion`, ítems combinados
  `compra_cotizacion_item` (vínculo al ítem de origen), documento por proveedor
  `compra_cotizacion_proveedor`. Crear/editar/consultar/enviar/anular + `GET /pendientes`.
  Correlativo `SC-AAAA-NNNNNN` (`compra_cotizacion_secuencia` con `LAST_INSERT_ID`).
  `CompraCotizacionService.completarMontos` calcula **monto y mínimo por la suma** de los montos
  de los pedidos combinados. **Estado nuevo `EN_COTIZACION`** del pedido.
- **tesoreria-compras**: puerto `CotizacionProcesoGateway` (Feign) y endpoints
  `/api/tesoreria/compras/cotizacion...` (permiso `compras.cotizaciones.gestionar`); resuelve el
  ejercicio en curso por defecto (`EjercicioActualGateway`).
- **tesoreria-frontend**: `CotizacionProcesoService` + pantalla `/cotizacion` (lista, armado con
  `ui-buscador-proveedor`, detalle con enviar/anular); ruta, menú y guards.

**Bug encontrado y corregido en vivo:** Hibernate ejecuta INSERTs antes que DELETEs en el mismo
flush → al reemplazar hijos se violaba `uk_compra_cotizacion_item`. Se fuerza
`itemRepository.flush()` / `proveedorRepository.flush()` tras los borrados.

## Fase 6C — Recepción y recomendación (implementada)

- **core**: detalle de respuesta `compra_cotizacion_proveedor_item`; endpoints
  `POST /{id}/proveedores/{cpId}/respuesta` y `.../no-cotiza`, `GET /{id}/comparacion`,
  `POST /{id}/recomendar` (exige el mínimo de respuestas o la excepción) y `POST /{id}/cerrar`.
  **Estado nuevo `PENDIENTE_ADJUDICACION`** del pedido al recomendar (D-COTZ-10).
- **tesoreria-compras**: métodos de fachada y endpoints equivalentes.
- **tesoreria-frontend**: carga de respuestas por proveedor, matriz de comparación y recomendación.

## Fuera de alcance (etapas siguientes)

- Envío real de correo (`sender-service`) y PDF del pedido de cotización.
- **Adjudicación** (Etapa 7): decisión de la autoridad competente a partir de
  `PENDIENTE_ADJUDICACION`; prototipo `adjudicacion.html`. Sin DDL nuevo aún.
- Formalización (OC/contrato) y Recepción.

## Orden

1 → 2 → 3 → 4, con tests en cada capa y `mvn verify` (core y compras) + `nx lint|build`.

## Riesgos

- **Acoplamiento cross-slice en core**: la composición (tramos × referencia en la resolución;
  pedido × mínimo en el snapshot) se hace en la capa de aplicación, siguiendo el patrón ya
  documentado (`CompraPedidoService` compone `CompraPedidoItemService`/`Historial`/`Secuencia`).
- **Configuración ausente**: el `estimar` no se bloquea (snapshot `null`); el gate estricto queda
  en el proceso (6B) para no frenar la operación por una configuración faltante.
