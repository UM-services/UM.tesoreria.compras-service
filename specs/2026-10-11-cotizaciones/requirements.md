# Requirements — Cotizaciones (pedidos de cotización)

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-11 · Estado: **en curso (Fase 6A)**
Registro de decisiones: `PLANIFICACION_COMPRAS.md` (raíz) **§14**.
Decisiones del circuito: **D-COTZ-1..11** (§14.10).

Global: [stack y convenciones](../tech-stack.md).

---

## Objetivo

Cerrar la etapa 3 del circuito de compras: **emitir los pedidos de cotización** a proveedores a
partir de los pedidos autorizados en presupuesto, **recibir y comparar** las cotizaciones y emitir
la **recomendación** que habilita la adjudicación.

## Alcance por fases

| Fase | Contenido | Estado |
|---|---|---|
| **6A** | **Aplicación del mínimo** de cotizaciones: resolver (tramos × referencia), snapshot al estimar y visualización. | ✅ |
| **6B** | **Emisión**: proceso de cotización (ítems combinados de varios pedidos + documentos por proveedor), envío previsto (sin correo) y trazabilidad. | ✅ |
| 6C | **Recepción y recomendación**: carga de respuestas, comparación y recomendación (gate a adjudicación). | ✅ |

## Modelo acordado (resumen)

- **Proceso de cotización** (`compra_cotizacion`, `SC-AAAA-NNNNNN`): agrupa ítems combinados de
  varios pedidos, el mínimo exigido y la recomendación.
- **Documento por proveedor** (`compra_cotizacion_proveedor`): el pedido de cotización que se
  envía a cada proveedor y la cabecera de su respuesta.
- **Detalle de la respuesta** (`compra_cotizacion_proveedor_item`): precio por ítem del proceso.
- **Trazabilidad**: cada ítem del proceso apunta al ítem de origen (`compra_pedido_item`).
- **Mínimo**: se recalcula por la **suma** de los montos estimados de los pedidos combinados
  (D-COTZ-3). Al estimar, cada pedido guarda su propio snapshot.

## Decisiones (D-COTZ — ver §14.10)

| ID | Decisión |
|---|---|
| D-COTZ-1 | Un documento por proveedor, agrupado en un proceso de cotización. |
| D-COTZ-2 | La etapa incluye las respuestas (carga, comparación, recomendación). |
| D-COTZ-3 | Mínimo por la **suma** de los montos estimados de los pedidos combinados. |
| D-COTZ-4 | Un ítem de pedido pertenece a una sola solicitud vigente. |
| D-COTZ-5 | Estado `EN_COTIZACION` en el pedido cuando todos sus ítems están en un proceso enviado. |
| D-COTZ-6 | Se admite cantidad parcial por ítem. |
| D-COTZ-7 | La acción `enviar` marca `ENVIADA` + `fecha_envio` + email snapshot, sin correo real. |
| D-COTZ-8 | Nombres `compra_cotizacion*` (proceso / documento por proveedor / detalle). |
| D-COTZ-9 | Recomendación manual (proveedor + fundamento), asistida por la comparación. |
| D-COTZ-10 | Al recomendar, el pedido pasa a `PENDIENTE_ADJUDICACION`. |
| D-COTZ-11 | Un pedido entra completo (todos sus ítems) en un proceso. |

## Requerimientos — Fase 6A

| ID | Requerimiento | Estado |
|---|---|---|
| REQ-COT-01 | `core` resuelve el mínimo de cotizaciones con los tramos (`compra_cotizacion_tramo`) y la referencia del ejercicio (`compra_referencia`). | ✅ |
| REQ-COT-02 | El mínimo se resuelve **fail-closed**: sin referencia o sin tramos ⇒ no se resuelve. | ✅ |
| REQ-COT-03 | Al **estimar** un pedido se persiste el snapshot `compra_pedido.cantidad_cotizaciones_minima`. | ✅ |
| REQ-COT-04 | Existe un endpoint del mínimo por pedido (snapshot o cálculo) expuesto por la fachada. | ✅ |
| REQ-COT-05 | El frontend muestra "Cotizaciones mínimas requeridas: N" en revisión de compras y detalle. | ✅ |
| REQ-COT-06 | Credibilidad: si la configuración falta, el pedido sigue estimándose; el **gate** estricto se aplica al armar el proceso (6B). | ✅ |
| REQ-COT-07 | Existe un **proceso de cotización** (`compra_cotizacion`) que combina ítems de varios pedidos, con el vínculo al ítem de origen. | ✅ |
| REQ-COT-08 | Cada proceso tiene **un documento por proveedor** (`compra_cotizacion_proveedor`) y un mínimo validado al enviar (o excepción con motivo). | ✅ |
| REQ-COT-09 | El **monto y el mínimo** del proceso se calculan por la **suma** de los montos de los pedidos combinados (D-COTZ-3). | ✅ |
| REQ-COT-10 | El pedido pasa a **`EN_COTIZACION`** cuando todos sus ítems integran un proceso enviado. | ✅ |
| REQ-COT-11 | El frontend permite listar, armar y enviar procesos (`/cotizacion`). | ✅ |

## Fuera de alcance (esta fase)

- Persistencia y pantallas del proceso de cotización y sus respuestas (6B/6C).
- Envío real de correo (`sender-service`) y PDF del pedido de cotización.
- Adjudicación y formalización.

## Bloqueos

Ninguno. El DDL de la etapa ya está aplicado (`V410__compras_cotizaciones.sql`).
