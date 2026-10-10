# Requirements — Fachada gateada de Gastos y Proveedores

**Feature 2** del [roadmap](../roadmap.md) · Abierto: 2026-10-09 · Estado: **en curso**

Global: [stack y convenciones](../tech-stack.md)

---

## Objetivo

Que las pantallas **Gastos** y **Proveedores** del `compras-client` consuman
`compras-service` en lugar de pegarle directo a `core-service`, de modo que el acceso quede
gateado por permiso **en el servidor** sin tocar el core (legacy intacto).

## Contexto (verificado)

- El core **no** puede llevar `@RequierePermiso` en sus endpoints de artículos/proveedores:
  son consumidos por el legacy (VB6/frontends viejos) que no envía identidad ni permisos
  ([AGENTS §Permisos](../AGENTS.md)).
- `compras-service` ya es cliente de core por Feign y ya tiene un patrón de gating
  (el `pedidoCompra` verifica el bundle efectivo y responde `403`), pero **no** tenía una
  forma declarativa reutilizable.
- El frontend llamaba a `/api/tesoreria/core/{articulo,proveedor,ubicacion,ubicacionArticulo,sheet}`
  directamente.

## Requerimientos

| ID | Requerimiento |
|---|---|
| REQ-FGP-01 | `compras-service` expone una fachada canónica `/api/tesoreria/compras/...` para **todo** lo que usan las pantallas: artículos (listar/buscar/nuevo/crear/editar/borrar), ubicaciones, imputaciones (ver/asignar), proveedores (listar/buscar/por CUIT/crear/editar/borrar) y la planilla de proveedores |
| REQ-FGP-02 | Cada endpoint exige su clave de permiso con `@RequierePermiso("...")` (PEP propio del servicio), evaluada contra el bundle efectivo de core; sin permiso `403`, sin identidad `401` |
| REQ-FGP-03 | El enforcement es **fail-closed** y se puede apagar por entorno (`APP_PERMISSIONS_ENFORCE`), sin afectar endpoints no anotados |
| REQ-FGP-04 | La fachada preserva el contrato JSON que el frontend ya consume (`PaginatedResponse{data,totalElements,totalPages,currentPage,pageSize}`, DTOs con `cuenta` anidada) y los estados de core (`400/404/409` → `ProblemDetail`) |
| REQ-FGP-05 | El `compras-client` deja de llamar a `/api/tesoreria/core/**` para estas pantallas y pasa a `/api/tesoreria/compras/**` |
| REQ-FGP-06 | El core **no** se modifica (ni endpoints ni gating) |
| REQ-FGP-07 | Los buscadores compartidos (`ui-buscador-cuenta-contable`, `ui-buscador-proveedor`) siguen consumiendo core: los usan otras apps |

## Mapa de claves

| Endpoint | Clave |
|---|---|
| `GET/POST articulo` (listar/buscar/ver) | `compras.gastos` |
| `GET articulo/new`, `POST articulo/` | `compras.gastos.crear` |
| `PUT articulo/{id}` | `compras.gastos.editar` |
| `DELETE articulo/{id}` | `compras.gastos.eliminar` |
| `GET ubicacion/`, `GET ubicacionArticulo/articulo/{id}` | `compras.gastos` |
| `POST ubicacionArticulo/` | `compras.gastos.imputar` |
| `GET proveedor/page`, `POST proveedor/search`, `GET proveedor/cuit/{cuit}`, `GET proveedor/{id}` | `compras.proveedores` |
| `POST proveedor/` | `compras.proveedores.crear` |
| `PUT proveedor/{id}` | `compras.proveedores.editar` |
| `DELETE proveedor/{id}` | `compras.proveedores.eliminar` |
| `GET sheet/generateProveedores` | `compras.proveedores.descargar` |

## Fuera de alcance

- Enforcement en core (`@RequierePermiso` allí) — prohibido por la regla legacy.
- Migrar los buscadores compartidos a compras.
- JWT real (M3): la identidad sigue siendo el header transitorio `X-User-Id`.

## Bloqueos

Ninguno.
