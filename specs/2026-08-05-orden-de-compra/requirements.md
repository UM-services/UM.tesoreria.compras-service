# Requirements — Orden de compra

**Feature 2** del [roadmap](../roadmap.md) · Abierto: 2026-08-05 · Estado: **planificado**

Global: [misión y reglas](../mission.md) · [stack y convenciones](../tech-stack.md)

---

## Objetivo

Construir el agregado `OrdenCompra`, dueño de la autorización de gasto previa a una
factura: cabecera, ítems, imputación por ítem, estados, persistencia propia y consultas.
La orden se desarrolla y valida sin depender de datos ni cambios en `core-service`.

## Decisión B11 — numeración de órdenes

Las órdenes se identifican públicamente como **`OC-AAAA-NNNNNN`**. El correlativo es
global para todo el servicio, reinicia cada año calendario y lo genera
`compras-service` de forma transaccional. Ejemplo: `OC-2026-000001`.

No se numera por sede: una referencia global es más simple de comunicar, auditar y buscar;
la sede sigue siendo un atributo consultable de la orden. La secuencia es propiedad de
este servicio y forma parte de sus tablas, no de `core-service` ni del cliente.

## Requerimientos funcionales

| ID | Requerimiento |
|---|---|
| REQ-OC-01 | Crear una orden con fecha de emisión, `proveedorId`, `sedeId`, detalle de ítems y la imputación de cada ítem. |
| REQ-OC-02 | Cada ítem conserva los identificadores externos que necesita (`articuloId` e identificadores de imputación) como referencias; no consulta ni valida maestros de core al crear o editar. |
| REQ-OC-03 | La aplicación calcula el importe total exclusivamente a partir de sus ítems; el cliente no puede fijar ni alterar el total de la cabecera. |
| REQ-OC-04 | Al crear, la orden recibe el siguiente número anual global `OC-AAAA-NNNNNN` y el estado `PENDIENTE_DE_APROBAR`. |
| REQ-OC-05 | La orden expone los estados `PENDIENTE_DE_APROBAR`, `APROBADA`, `ENVIADA`, `CUMPLIDA`, `FACTURA_PARCIAL`, `CUMPLIDA_PARCIAL` y `ANULADA`. |
| REQ-OC-06 | Los cambios de estado ocurren sólo mediante operaciones explícitas del dominio; una actualización de datos nunca puede editar el campo de estado. |
| REQ-OC-07 | Las transiciones válidas y sus causas de rechazo quedan definidas y cubiertas por tests. La autorización por rol y monto se agrega en la feature 3, sin debilitar la máquina de estados. |
| REQ-OC-08 | Consultar una orden por su identificador interno o número público devuelve la cabecera, el total, el estado, todos los ítems y sus imputaciones en una única respuesta. |
| REQ-OC-09 | Listar órdenes permite combinar filtros opcionales por estado, `proveedorId`, `sedeId` y rango inclusivo de fecha de emisión. |
| REQ-OC-10 | La edición de datos de negocio se permite únicamente mientras la orden está `PENDIENTE_DE_APROBAR`; recalcula el total y no modifica su número. |
| REQ-OC-11 | No existe borrado físico expuesto: la baja de una orden se representa por la transición explícita a `ANULADA`, preservando su historial. |
| REQ-OC-12 | Los endpoints usan la ruta canónica única `/api/tesoreria/compras/ordenCompra`; no se agrega ruta corta ni dual. |
| REQ-OC-13 | El controlador recibe y devuelve DTOs; el dominio no conoce Spring, JPA, Jackson ni DTOs. |
| REQ-OC-14 | Se mantiene la separación hexagonal: controller → fachada/casos de uso → puertos → adaptadores de persistencia. |
| REQ-OC-15 | Las tablas son propiedad de compras. Hibernate opera con `ddl-auto: none`; no crea ni modifica el esquema. |
| REQ-OC-16 | Antes de desplegar persistencia se documenta el DDL propuesto, su justificación y el caso de uso que lo requiere, para solicitar su aplicación al DBA conforme a D9. |

## Transiciones de estado

La implementación definirá este grafo cerrado. Una transición fuera del grafo devuelve un
error de dominio y no altera la orden.

```text
PENDIENTE_DE_APROBAR ──aprobar──> APROBADA ──enviar──> ENVIADA
         │                               │                 │
         └──────────anular───────────────┴────anular────────┘
                                                              │
                                  ┌───────────────────────────┼──────────────────────────┐
                                  ▼                           ▼                          ▼
                           FACTURA_PARCIAL           CUMPLIDA_PARCIAL               CUMPLIDA
                                  │                           │
                                  └───────────────actualizar cumplimiento───────────┘
                                                              │
                                                           CUMPLIDA
```

`ANULADA` y `CUMPLIDA` son terminales. La feature 3 restringirá quién puede aprobar o
anular; esta feature garantiza desde ahora que ninguna actualización común pueda hacerlo.

## Requerimientos no funcionales

- Cobertura de líneas de al menos 80 %, comprobada por JaCoCo y fallando el build.
- Tests unitarios aislados, sin base de datos ni red, para dominio, casos de uso y mappers.
- Consultas sin N+1 para el detalle: el adaptador carga la orden y sus ítems/imputaciones
  en la misma operación de lectura.
- Los importes se representan con `BigDecimal`; no se usan `float` ni `double`.
- Todas las referencias a proveedores, artículos e imputaciones son identificadores, no
  relaciones JPA a tablas de `core-service`.

## Fuera de alcance

- Validar que proveedor, artículo, sede o imputación existan en core.
- Resolver nombres o datos de presentación desde core.
- Aprobación por monto, roles, umbrales, rechazos y trazabilidad de aprobadores (feature 3).
- Facturas, devengamiento, pagos, envío de mails/PDFs, gateway y cambios en otros repositorios.
- Migraciones automáticas: el DDL se consensúa y solicita al DBA.

## Bloqueos y decisiones pendientes

La numeración B11 queda resuelta en este documento. Antes de implementar el adaptador de
persistencia se debe consensuar el DDL físico con el DBA, incluida la ubicación del
esquema de compras; no es autorización para que Hibernate modifique tablas.
