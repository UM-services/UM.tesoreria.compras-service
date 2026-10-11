# Plan — Especificaciones enriquecidas de los ítems del pedido

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-10
Decisiones D-MD en [requirements.md](requirements.md).

---

## Tareas (implementadas)

1. **Nueva lib `@tesoreria/ui-rich-text`** (`type:ui`, `scope:shared`) con el componente
   `ui-editor-enriquecido`: envuelve `quill-editor` de `ngx-quill@31`, con barra por defecto estilo
   procesador de textos, y expone `[(html)]`. Se crea como lib propia (no dentro de `ui-layout`)
   para que `quill`/`ngx-quill` viajen en un **chunk lazy** y no entren al bundle inicial.
2. **`ui-layout` → `ui-texto-enriquecido`**: pasa de render Markdown a **render HTML sanitizado
   con DOMPurify** (carga diferida); conserva el colapsado y respeta el texto plano legacy
   (saltos de línea → `<br>`). Se elimina `marked`.
3. **`feature-pedido-compra` → `pedido-form`**:
   - El modal de edición usa `<ui-editor-enriquecido [(html)]="editorValor" />` para
     especificaciones y necesidad (se quitan la barra propia, la vista previa y el `textarea`).
   - La celda de especificaciones de cada ítem es un **bloque clickeable** con vista previa
     (o placeholder «Agregar especificaciones…») que abre el editor.
   - El campo necesidad muestra su vista previa con `ui-texto-enriquecido`.
4. **`apps/compras`**: se agrega `node_modules/quill/dist/quill.snow.css` a los estilos globales.

## Orden

1 → 2 → 3 → 4. Todo es frontend; el backend no cambia.

## Riesgos (resueltos)

- **Presupuesto de bundle**: resuelto con la lib separada (Quill en chunk lazy, ~205 kB) y DOMPurify
  con `import()` diferido. Bundle inicial de `compras` ~480 kB (límite 500 kB).
- **Riesgo XSS**: resuelto con sanitización DOMPurify antes de `bypassSecurityTrustHtml`.
- **Compatibilidad de datos**: los textos legacy (sin etiquetas) se muestran con saltos de línea.

## Docs

- `PLANIFICACION_COMPRAS.md` (raíz) §12 · esta carpeta · `CHANGELOG.md` de `tesoreria-frontend`.
