# Requirements — Especificaciones enriquecidas de los ítems del pedido

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-10 · Estado: **implementada (editor WYSIWYG)**
**Alcance real:** cambio de **frontend** (`tesoreria-frontend`: `feature-pedido-compra` +
`ui-rich-text` + `ui-layout`). Se registra acá por ser parte del circuito de compras; no requiere
cambios de backend.

Global: [stack y convenciones](../tech-stack.md) · Decisiones del circuito: `PLANIFICACION_COMPRAS.md` §12 (raíz).

---

## Objetivo

Que cargar y leer las **especificaciones** de cada ítem del pedido sea cómodo. Originalmente la
columna `especificaciones` (TEXT) admitía contenido libre pero se cargaba en un `<input>` de una
línea y se mostraba en un `<td>` que colapsa el whitespace.

## Contexto (verificado)

- `tesium.compra_pedido_item.especificaciones` es `TEXT` (`utf8mb4`): el motor admite contenido
  largo. El límite era de UI, no de datos.
- `CompraPedidoItem` (core) y su paso por la fachada `tesoreria-compras` tratan el campo como
  texto opaco: **no** se cambia el contrato.
- `pedido-revision` y `pedido-presupuesto` **no** muestran especificaciones: fuera de alcance.
- El único módulo del frontend con campos de texto largo es compras; el resto de los `textarea`
  son motivos cortos de rechazo/descarte (`varchar(500)`) que no se convierten a HTML.

## Decisiones (D-MD)

| ID | Decisión |
|---|---|
| D-MD-1 | El formato persistido es **HTML** (el que produce el editor) en la columna `especificaciones TEXT` existente. Sin DDL; los datos viejos (texto plano) se muestran tal cual. |
| D-MD-2 | Editor **WYSIWYG**: Quill 2 vía `ngx-quill@31`, con barra estilo procesador de textos (títulos, negrita/itálica, listas, alineación, enlaces). |
| D-MD-3 | El editor vive en un **modal por ítem**; la fila muestra una **vista previa clickeable**. |
| D-MD-4 | El **render** de solo lectura es `ui-texto-enriquecido` (sanitiza HTML) en **`ui-layout`**; el **editor** vive en la lib **`ui-rich-text`**. |
| D-MD-5 | Los reportes PDF/Excel (report-service) quedan para una **fase posterior**. |
| D-MD-6 | **Sin límite** de longitud adicional (se conserva el de la columna `TEXT`). |
| D-MD-7 | El render se **sanitiza** con **DOMPurify** antes de inyectarlo. |
| D-MD-8 | El mismo editor se aplica a **especificaciones y a `necesidad`**. |
| D-MD-9 | El editor se aísla en su propia lib para que `quill`/`ngx-quill` queden en un **chunk lazy** y no en el bundle inicial. |

## Implementación

- Lib **`@tesoreria/ui-rich-text`** → `ui-editor-enriquecido` (Quill 2 vía `ngx-quill@31`;
  expone `[(html)]`). El CSS del tema de Quill se carga global en la app `compras`.
- `feature-pedido-compra`: el formulario edita especificaciones y necesidad con ese editor en un
  modal; la fila de ítem muestra una vista previa clickeable. El detalle y las vistas usan
  `ui-texto-enriquecido`.
- `@tesoreria/ui-layout` → `ui-texto-enriquecido`: renderiza **HTML sanitizado con DOMPurify**
  (carga diferida) y respeta el texto plano legacy (saltos de línea → `<br>`); colapsa el
  contenido extenso.

## Requerimientos

| ID | Requerimiento | Estado |
|---|---|---|
| REQ-ESP-01 | El formulario edita especificaciones y necesidad con un **editor WYSIWYG** (modal) | ✅ |
| REQ-ESP-02 | La fila de ítem muestra una **vista previa clickeable** de las especificaciones | ✅ |
| REQ-ESP-03 | El detalle muestra especificaciones y necesidad y **colapsa** el texto extenso | ✅ |
| REQ-ESP-04 | El render de solo lectura es un **componente compartido** (`ui-texto-enriquecido`) reutilizable | ✅ |
| REQ-ESP-05 | Sin cambios de esquema, de contrato ni de backend | ✅ |
| REQ-ESP-06 | El editor es **WYSIWYG** (formato visible, sin sintaxis) con barra de herramientas | ✅ |
| REQ-ESP-07 | El render HTML está **sanitizado** (sin XSS) | ✅ |
| REQ-ESP-08 | Datos legacy (texto plano) se siguen mostrando correctamente | ✅ |

## Fuera de alcance

- Plantillas de especificaciones y autocompletado desde `articulo`.
- Reportes PDF/Excel (D-MD-5).
- Cambios en `core-service` o en la fachada `compras-service`.
- Convertir a WYSIWYG los motivos cortos de rechazo/descarte.

## Bloqueos

Ninguno.
