# Validation — Fachada gateada de Gastos y Proveedores

**Feature 2** del [roadmap](../roadmap.md) · Abierto: 2026-10-09

---

## Criterios

| ID | Criterio | Verificación |
|---|---|---|
| VAL-FGP-01 | `mvn verify` del `compras-service` verde, con JaCoCo (80% líneas bundle / 70% por clase) | `mvn -B verify` |
| VAL-FGP-02 | Cada endpoint de la fachada tiene su `@RequierePermiso` con la clave del mapa | revisión + tests del interceptor |
| VAL-FGP-03 | Sin `X-User-Id` → `401`; con identidad sin la clave → `403`; con la clave → pasa | `RequierePermisoInterceptorTest` |
| VAL-FGP-04 | El adapter traduce `404/409/4xx/5xx` a excepciones de dominio y nunca filtra `FeignException` | tests de adapter |
| VAL-FGP-05 | `PaginatedResponse` y los DTOs conservan el shape que consume el front | tests de mapper/controller |
| VAL-FGP-06 | `nx run-many -t lint test build --projects=compras,feature-gastos,feature-proveedores` verde | comando |
| VAL-FGP-07 | El frontend no conserva ninguna referencia a `/api/tesoreria/core` en las dos pantallas | `grep` |
| VAL-FGP-08 | El core no cambia | `git status` del `tesoreria-core` sin cambios nuevos |

## Evidencia a adjuntar al cierre

- Salida de `mvn -B verify` (coverage OK).
- Salida de `nx run-many` (lint/test/build).
- Smoke manual: `GET /api/tesoreria/compras/articulo/tipo/gasto/page` con y sin
  `X-User-Id`/permiso por gateway.
