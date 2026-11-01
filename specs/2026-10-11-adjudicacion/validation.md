# Validation — Adjudicación por monto y bandeja de respuestas

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-11
Requirements: [requirements.md](requirements.md) · Plan: [plan.md](plan.md).

---

## Pieza A — automatizada

- **core:** `mvn test` verde (suite completa); casos nuevos del slice `compraCotizacion`:
  `CompraCotizacionServiceTest#respuestasPendientesAplanaSoloDocumentosEnviados`,
  `CompraCotizacionControllerTest#proveedoresPendientesDevuelveLaLista`,
  `CompraCotizacionDtoMapperTest#toResponseProveedorPendienteMapea`.
- **tesoreria-compras:** `mvn verify` verde con los gates JaCoCo (80/75/70):
  `CotizacionProcesoServiceTest`, `CotizacionProcesoControllerTest`,
  `CotizacionProcesoDtoMapperTest`, `CoreCompraCotizacionFeignClientAdapterTest`.
- **tesoreria-frontend:** `npx nx build compras` y `npx nx run-many -t lint -p compras,feature-pedido-compra`
  verdes.

## Pieza A — manual (en vivo)

1. Levantar `tesoreria-core-service`, `tesoreria-compras-service` y el cliente compras
   (`docker compose -p um up -d --build ...`).
2. `GET /api/tesoreria/core/compraCotizacion/proveedores-pendientes?ejercicioId=15` devuelve los
   documentos `ENVIADA` (vacío si todos están respondidos).
3. `GET /api/tesoreria/compras/cotizacion/proveedores-pendientes` con `X-User-Id` de un usuario con
   `compras.cotizaciones.gestionar` responde `200`; sin el permiso, `403`.
4. En la app compras → **Cotizaciones pedidas**: se listan los pendientes; "Cargar valores" guarda la
   respuesta (el documento pasa a `RESPONDIDA` y desaparece de la bandeja); "No cotiza" lo marca.

## Pieza B — implementada

**Automática:** `mvn test` en core (748 tests) y `mvn verify` en compras (386 tests, gates JaCoCo
80/75/70) verdes; `npx nx build compras` y lint de `compras`/`feature-pedido-compra` verdes. Casos
nuevos: `AdjudicarCompraCotizacionUseCaseImplTest`, `CompraCotizacionServiceTest` (adjudicar/devolver/
límite excedido), `CompraCotizacionControllerTest` (adjudicar/409/404), `CotizacionProcesoServiceTest`
(límite, oferta inexistente, no recomendado), `AdjudicacionCotizacionControllerTest`,
`CoreCompraCotizacionFeignClientAdapterTest`.

**Manual (en vivo):** aplicar `V411`; levantar core/compras/cliente; con un usuario con
`compras.adjudicacion.autorizar`, `GET /cotizacion/adjudicacion/bandeja` lista los procesos
`RECOMENDADA`; `POST /{id}/adjudicar` con un total ≤ límite deja el proceso `ADJUDICADA` y el pedido
`ADJUDICADO`; con total > límite responde `403 LIMITE_AUTORIZACION_EXCEDIDO`; sin el permiso, `403`.
