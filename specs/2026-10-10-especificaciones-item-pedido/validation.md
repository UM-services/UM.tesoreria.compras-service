# Validation — Especificaciones enriquecidas de los ítems del pedido

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-10

---

## Criterios

| ID | Criterio | Verificación |
|---|---|---|
| VAL-ESP-01 | `ui-rich-text`, `ui-layout` y `feature-pedido-compra` lint/test/build en verde | `nx run-many -t lint test build` |
| VAL-ESP-02 | El formulario abre el editor WYSIWYG para especificaciones (por ítem) y para necesidad; al confirmar, el HTML queda en el modelo | prueba manual + revisión |
| VAL-ESP-03 | El detalle renderiza el HTML y colapsa el texto extenso | prueba manual |
| VAL-ESP-04 | Sin cambios de backend: `core-service` y `compras-service` sin modificaciones | `git status` de ambos repos |
| VAL-ESP-05 | Datos legacy (texto plano) se siguen mostrando correctamente (con saltos de línea) | `texto-enriquecido.spec.ts` + prueba con un pedido existente |
| VAL-ESP-06 | No se rompe el presupuesto de bundle de la app `compras` | `nx build compras` sin warning de límite |
| VAL-ESP-07 | El render HTML sanea scripts/atributos peligrosos | `texto-enriquecido.spec.ts` (caso `<script>`/`onerror`) |

## Resultado

- `nx run-many -t lint test build` (todos los proyectos): **44/44 OK**.
- `ui-layout` tests: **36/36** (incluye `texto-enriquecido.spec.ts`: render HTML, saltos de línea
  legacy, sanitización XSS, colapso).
- `nx build compras`: **OK**; bundle inicial **479.83 kB** (límite 500 kB); `ngx-quill`/`quill`
  (~205 kB) y DOMPurify en **chunks lazy**.
- Sin cambios de código en `tesoreria-core` ni en `tesoreria-compras` (solo esta carpeta de docs).
- Pendiente de prueba manual en vivo (VAL-ESP-02/03/05) por el gateway.

## Evidencia a adjuntar al cierre

- Salida de `nx run-many` (lint/test/build).
- Captura del editor WYSIWYG en el modal y del detalle renderizado.
- `git status` de `tesoreria-core` y `tesoreria-compras` sin cambios de código (solo esta carpeta).
