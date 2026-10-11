# Requirements — Adjudicación por monto y bandeja de respuestas

**Feature** del [roadmap](../roadmap.md) · Abierto: 2026-10-11 · Estado: **Piezas A y B ✅**
Registro de decisiones: `PLANIFICACION_COMPRAS.md` (raíz) **§15**.
Decisiones del circuito: **D-ADJ-1..8** (§15.2).

Global: [stack y convenciones](../tech-stack.md).

---

## Objetivo

Cerrar la etapa 7 del circuito de compras: la **decisión de la autoridad competente** sobre la
cotización **recomendada**, con el **mismo esquema de valores que los pedidos** (perfiles de
autoridad × referencia del ejercicio). Se reparte en dos piezas independientes.

## Alcance por piezas

| Pieza | Contenido | Estado |
|---|---|---|
| **A** | **Bandeja "Cotizaciones pedidas"**: vista transversal de los documentos de proveedor en `ENVIADA`, para cargar los valores de las cotizaciones enviadas sin entrar proceso por proceso (reutiliza la carga de respuestas de la fase 6C). Sin DDL. | ✅ |
| **B** | **Adjudicación por monto**: `POST .../adjudicar` (adjudicar / devolver a compras / no adjudicar con fundamento), autoridad por `MAX(multiplico) × referencia`, tabla `compra_adjudicacion`, permiso `compras.adjudicacion.autorizar`. | ✅ |

## Modelo acordado (resumen)

- **Pieza A** aplana, por ejercicio, los documentos `compra_cotizacion_proveedor` con estado
  `ENVIADA` de **todos** los procesos, con el nº de proceso, el proveedor, la fecha de envío y los
  ítems del proceso. La carga reutiliza `POST /cotizacion/{id}/proveedores/{cpId}/respuesta` y
  `.../no-cotiza` (fase 6C). Aditivo, sin cambios de contrato previos.
- **Pieza B** reutiliza `compra_referencia`, `compra_autoridad_perfil` y `compra_autoridad_usuario`
  (§11); el monto evaluado es el **total de la cotización recomendada**; el enforcement es
  **fail-closed** en la fachada. La adjudicación se persiste en una tabla nueva (`compra_adjudicacion`).

## REQ

- **REQ-BAN-1**: `GET /api/tesoreria/compras/cotizacion/proveedores-pendientes` lista los documentos
  de proveedor en `ENVIADA` del ejercicio (en curso si no llega), con permiso
  `compras.cotizaciones.gestionar`.
- **REQ-BAN-2**: la carga y el "no cotiza" reutilizan los endpoints de la fase 6C (sin duplicar
  lógica de persistencia en la fachada).
- **REQ-ADJ-1**: `POST /cotizacion/{id}/adjudicar` valida el límite de la autoridad (fail-closed,
  `403 LIMITE_AUTORIZACION_EXCEDIDO`) y registra el acto con fundamento.
- **REQ-ADJ-2**: el acto deja el proceso `ADJUDICADA`/`DEVUELTA_A_COMPRAS`/`NO_ADJUDICADA` y el
  pedido cubierto `ADJUDICADO` al adjudicar.
- **REQ-ADJ-3**: permiso nuevo `compras.adjudicacion.autorizar` (catálogo + wiring en el mismo cambio).
