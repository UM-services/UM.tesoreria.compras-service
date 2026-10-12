# Validation — Cotizaciones (pedidos de cotización)

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-11

---

## Criterios — Fase 6A

| ID | Criterio | Verificación |
|---|---|---|
| VAL-COT-01 | El resolvedor devuelve el mínimo correcto por tramo (incluido el tramo abierto) | `ResolutorMinimoCotizacionesTest` |
| VAL-COT-02 | Sin referencia o sin tramos, el mínimo **no** se resuelve (`Optional.empty` / `409`) | tests del service y del controller |
| VAL-COT-03 | Al estimar, el pedido persiste `cantidad_cotizaciones_minima` | `CompraPedidoServiceTest` / `CompraPedidoUseCasesTest` |
| VAL-COT-04 | El endpoint del mínimo por pedido responde snapshot o cálculo | test del controller de core + fachada |
| VAL-COT-05 | La fachada exige `compras.estimar` y traduce `COTIZACIONES_NO_CONFIGURADAS` a `409` | test de la fachada |
| VAL-COT-06 | El frontend muestra el mínimo en revisión y detalle | lint/build + prueba manual |
| VAL-COT-07 | `mvn verify` de `core` y `compras` en verde (gates JaCoCo) | CI local |
| VAL-COT-08 | `nx lint|test|build` de `compras` y `feature-pedido-compra` en verde | CI local |

## Criterios — Fase 6B

| ID | Criterio | Verificación |
|---|---|---|
| VAL-COT-10 | El proceso combina ítems de varios pedidos y conserva el vínculo al ítem de origen | `CompraCotizacionTest` / `CompraCotizacionUseCasesTest` |
| VAL-COT-11 | `enviar` reserva `SC-AAAA-NNNNNN` y valida el mínimo (o excepción con motivo) | `CompraCotizacionServiceTest` / `CompraCotizacionTest` |
| VAL-COT-12 | El monto y el mínimo del proceso se calculan por la suma de los pedidos | `CompraCotizacionServiceTest` |
| VAL-COT-13 | El pedido pasa a `EN_COTIZACION` cuando sus ítems quedan cubiertos | `CompraPedidoTest` / `CompraPedidoServiceTest` / `CompraCotizacionServiceTest` |
| VAL-COT-14 | La fachada exige `compras.cotizaciones.gestionar` y traduce los errores de core | `CoreCompraCotizacionFeignClientAdapterTest` / controller |
| VAL-COT-15 | El frontend lista, arma y envía procesos | lint/build + prueba manual |

## Criterios — Fase 6C

| ID | Criterio | Verificación |
|---|---|---|
| VAL-COT-20 | Se carga la respuesta de un proveedor (cabecera + precio por ítem) y queda `RESPONDIDA` | `CompraCotizacionTest` / `CompraCotizacionUseCasesTest` |
| VAL-COT-21 | `no-cotiza` marca el documento del proveedor | `CompraCotizacionUseCasesTest` |
| VAL-COT-22 | La comparación expone los precios por ítem de cada proveedor | prueba en vivo + `getById` con hijos |
| VAL-COT-23 | `recomendar` exige el mínimo de respuestas (o excepción) y pasa el proceso a `RECOMENDADA` | `CompraCotizacionTest` |
| VAL-COT-24 | Al recomendar, los pedidos cubiertos pasan a `PENDIENTE_ADJUDICACION` | `CompraCotizacionServiceTest` / `CompraPedidoTest` |
| VAL-COT-25 | `cerrar` deja el proceso en `CERRADA` | `CompraCotizacionTest` / `CompraCotizacionUseCasesTest` |

## Resultado

**Fases 6A, 6B y 6C — verificadas en vivo (2026-10-11).**

- **`mvn verify`**: core **733 tests**, compras **363 tests**, 0 fallos (gates JaCoCo 80/75/70).
- **`nx lint|build|test`**: `feature-pedido-compra`, `compras` y `feature-administrador` verdes.
- **Prueba en vivo (por el gateway):**
  - 6A: tramos (150k→1, 500k→2, 900k→3; sin referencia → 409), fachada 200/401/403 y
    estimar 900.000 → `cantidadCotizacionesMinima = 3`.
  - 6B: proceso combinando ítems de dos pedidos (monto = suma 133.456,78; mínimo 1) → `enviar`
    (`SC-2026-000001`, proveedores `ENVIADA`) → pedidos 1 y 2 a `EN_COTIZACION`; `pendientes` vacío.
  - 6C: respuestas de dos proveedores (`RESPONDIDA`, 3 precios cada uno), comparación, `recomendar`
    (proceso `RECOMENDADA`, pedidos a `PENDIENTE_ADJUDICACION`) y `cerrar` (`CERRADA`); re-recomendar
    sobre un proceso no enviado → `409`.

**Bug corregido en vivo (6B):** orden INSERT/DELETE de Hibernate en el reemplazo de hijos
(`flush()` tras los borrados).

Pendiente: prueba manual por la UI (cargas de respuesta y recomendación) y el envío real de correo
(fuera de alcance).

## Evidencia a adjuntar al cierre

- Salida de `mvn verify` (core y compras).
- Salida de `nx lint|build`.
- Captura de "Cotizaciones mínimas requeridas: N" en revisión y detalle.
