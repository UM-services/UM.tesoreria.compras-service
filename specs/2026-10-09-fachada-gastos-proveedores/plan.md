# Plan — Fachada gateada de Gastos y Proveedores

**Feature 2** del [roadmap](../roadmap.md) · Abierto: 2026-10-09

---

## Tareas

1. **PEP propio** en `configuration/security`: `@RequierePermiso`,
   `RequierePermisoInterceptor` (identidad por `X-User-Id` + `PermisoGateway`),
   `PermissionWebConfig` (activo por defecto, `APP_PERMISSIONS_ENFORCE`) y
   `SecurityExceptionHandler` (`403`/`401`).
2. **Slice `articulo`**: sumar `getPaginatedByTipo`, `getNew`, `create`, `update`, `delete`
   al puerto de salida, un caso de uso por operación, DTO de request, mapper y endpoints
   anotados. El adapter conserva el mapeo y traduce `404/409/4xx/5xx`.
3. **Slice `proveedor`**: sumar `getPaginated`, `search`, `getByCuit`, `create`, `update`,
   `delete`; `ProveedorController` nuevo en `/api/tesoreria/compras/proveedor`.
4. **Slice `ubicacion`**: `GET /` (selector de imputación).
5. **Slice `ubicacionArticulo`**: `GET /articulo/{id}` y `POST /` (asignar imputación).
6. **Slice `sheet`**: `GET /generateProveedores` que reenvía el binario de core.
7. **Frontend**: cambiar las bases de `feature-gastos` y `feature-proveedores` a
   `/api/tesoreria/compras`.
8. **Docs**: CHANGELOG (`[Unreleased]`) y esta carpeta.

## Orden

El PEP y los slices son independientes entre sí; el frontend va al final.

## Riesgos

- **Duplicación de mapeo** core↔fachada: se acepta para preservar el contrato del front.
- **Doble salto** (front → compras → core) por request: aceptable; el tráfico de estas
  pantallas es administrativo.
- El gating sigue siendo de **UX + PEP transitorio** hasta el JWT (M3).
