# Validation — Recepción y conformidad (fachada)

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-14
Requirements: [requirements.md](requirements.md) · Plan: [plan.md](plan.md).

---

## Automatizada (ejecutada)

- **tesoreria-compras:** `mvn -B verify` **verde** (480 tests) con los gates JaCoCo (80% línea / 75%
  rama del bundle y 70% línea por clase). Casos nuevos:
  - `RecepcionServiceTest` (ejercicio en curso, filtros, delegaciones).
  - `CoreCompraRecepcionFeignClientAdapterTest` (mapeo de pendientes/listado/detalle/registro/cierre y
    traducción de `404`/`400`/`409`/`503`).
  - `RecepcionControllerTest` (endpoints, actor por header, cierre `204`).
  - `RecepcionDtoMapperTest` y `RecepcionExceptionHandlerTest` (`404`/`400`/`409`).
- **tesoreria-core:** `mvn -B verify` **verde** (873 tests, gates JaCoCo) — el detalle está en
  `PLANIFICACION_COMPRAS.md` §18.10 (slice `compraRecepcion`, estados del pedido y `V413`).
- **tesoreria-frontend:** `npx nx lint feature-pedido-compra` y `npx nx build compras`
  (`--skip-nx-cache`) verdes.

## Manual (en vivo — ejecutada el 2026-10-14)

Contenedores reconstruidos (`docker compose -p um up -d --build tesoreria-core-service
tesoreria-compras-service`) sobre la base de desarrollo con `V412`+`V413` aplicados; la clave
`compras.recepcionar` se otorgó a `dquinteros` (38) con `PUT /usuarioPermiso/user/38/permiso/40`.

| Paso | Resultado |
|---|---|
| `POST /api/tesoreria/core/compraFormalizacion` (proceso `SC-2026-000001`) | OC `OC-2026-000001`; pedidos a `FORMALIZADO` |
| `GET .../compraRecepcion/pendientes?ejercicioId=15` | la OC con saldo por ítem |
| `POST .../compraRecepcion` (parcial, remito `R-PRUEBA-1`) | `RC-2026-000001`; ítem `recibido 2.50 / saldo 2.50`; pedido `RECEPCION_PARCIAL` |
| `POST .../compraRecepcion` con cantidad > saldo | `400` ("supera el saldo pendiente") |
| `POST .../compraRecepcion` (complementaria, remito `R-PRUEBA-2`) | `RC-2026-000002`; OC `completo: true`, saldos 0, fuera de la bandeja; pedidos `RECEPCIONADO` |
| `GET .../compraPedidoHistorial/1` | `FORMALIZADO → RECEPCION_PARCIAL → RECEPCIONADO` |
| `POST .../formalizacion/1/cerrar` sin motivo / con motivo | `400` / `204` |
| `GET /api/tesoreria/compras/recepcion/por-formalizacion/1` (`X-User-Id: 38`) | `200` con la OC y sus dos entregas |
| `GET /api/tesoreria/compras/recepcion/{id}` sin permiso asignado / sin identidad | `403` (`Permiso requerido: compras.recepcionar`) / `401` |

La UI se validó por `lint` + `build`; el recorrido visual de la app `compras` (**8. Recepción**)
queda para la revisión funcional con el cliente reconstruido.
