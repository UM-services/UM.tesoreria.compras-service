# Plan — Orden de compra

**Feature 2** · [requirements](requirements.md) · [validation](validation.md) ·
[roadmap](../roadmap.md)

`[compras]` se hace en este repositorio · `[DBA]` requiere el circuito de D9

---

## Objetivo y resultado esperado

Entregar una orden de compra persistida, numerada, con ítems e imputaciones, máquina de
estados explícita, endpoints canónicos y consultas de detalle/listado. El detalle se
obtiene completo desde compras, sin encadenar llamadas a `core-service`.

## Alcance

- Agregado `ordencompra` y sus puertos de entrada/salida.
- Persistencia propia de cabecera, ítems, imputaciones y secuencia anual.
- Creación, consulta, actualización de pendientes, anulación y comandos de transición.
- Listado por estado, proveedor, sede y fecha.
- DTOs, mappers, manejo de errores, tests y documentación D12.

## Fuera de alcance

- Roles y aprobación por monto; se integran como feature 3 sobre los comandos de estado.
- Cualquier lectura/escritura directa de tablas de core.
- Facturas, pagos, mails, PDFs y gateway.
- Aplicación automática de DDL o migraciones desde la aplicación.

## Arquitectura propuesta

`OrdenCompra` es el agregado de dominio y contiene ítems, imputaciones, total calculado y
la máquina de estados. Los casos de uso dependen de un único puerto de repositorio. El
adaptador JPA implementa ese puerto y usa entidades y mapper exclusivos de infraestructura.
Los controladores sólo convierten DTOs y delegan en la fachada de aplicación.

El número público se reserva en la misma transacción de creación mediante una secuencia
anual propiedad de compras, de modo que dos altas concurrentes no repitan
`OC-AAAA-NNNNNN`.

## T1 — Contrato de datos y base `[compras]` / `[DBA]`

- [x] 1.1 Definir atributos obligatorios de cabecera, ítem e imputación y el contrato de
      los DTOs, sin incorporar datos maestros de core.
- [x] 1.2 Elaborar el documento de cambio de base: tablas, columnas, tipos, claves,
      índices, restricciones, secuencia anual y justificación por caso de uso.
- [ ] 1.3 Consensuar el DDL y solicitar al DBA su aplicación; mantener `ddl-auto: none`.
- [x] 1.4 Incorporar las dependencias de MySQL/JPA y la configuración por variables que
      correspondan, sin valores de conexión versionados.

## T2 — Dominio y casos de uso `[compras]`

- [x] 2.1 Modelar `OrdenCompra`, `OrdenCompraItem`, imputación, estado y reglas de total.
- [x] 2.2 Implementar la máquina de estados y sus transiciones explícitas con excepciones
      de dominio para transiciones inválidas.
- [x] 2.3 Definir un puerto de entrada por operación: crear, obtener, listar, actualizar,
      aprobar, enviar, registrar cumplimiento/factura parcial y anular.
- [x] 2.4 Definir el puerto de repositorio, incluida la reserva atómica de numeración anual.
- [x] 2.5 Implementar los casos de uso y la fachada de aplicación como delegaciones.

## T3 — Persistencia `[compras]`

- [x] 3.1 Crear entidades JPA de cabecera, ítems, imputaciones y secuencia, separadas del
      dominio.
- [x] 3.2 Implementar repositorios Spring Data, mapper entidad↔dominio y el adapter del
      puerto de salida.
- [x] 3.3 Configurar la lectura de detalle para recuperar ítems e imputaciones sin N+1.
- [x] 3.4 Implementar filtros opcionales y rango inclusivo de fecha de emisión.

## T4 — API HTTP `[compras]`

- [x] 4.1 Crear los DTOs request/response y mappers dominio↔DTO.
- [x] 4.2 Exponer CRUD lógico, consultas y comandos de transición bajo
      `/api/tesoreria/compras/ordenCompra`.
- [x] 4.3 Agregar validación de requests y un manejador de errores de dominio consistente.
- [x] 4.4 Documentar el contrato en OpenAPI y verificar que no existen rutas duales.

## T5 — Calidad y documentación `[compras]`

- [x] 5.1 Cubrir reglas de total, numeración y todas las transiciones del dominio con tests
      unitarios.
- [x] 5.2 Cubrir casos de uso mockeando el puerto de salida, y mappers con datos de borde.
- [x] 5.3 Ejecutar tests, verificación JaCoCo, compilación y las puertas de
      [validation.md](validation.md).
- [x] 5.4 Mantener el caso de uso, diagramas de secuencia y hexagonal sincronizados con la
      implementación.

## Riesgos y decisiones

| Tema | Decisión / mitigación |
|---|---|
| B11 | Resuelto: `OC-AAAA-NNNNNN`, secuencia global anual generada transaccionalmente por compras-service. |
| Concurrencia | La reserva de número ocurre en la transacción de creación y tiene restricción única en base. |
| Datos externos | Sólo se guardan IDs; no hay FKs ni llamadas a core durante alta/edición. |
| DDL | El DBA aplica tablas tras recibir el documento; Hibernate no administra esquema. |
| Autorización | Feature 2 preserva las transiciones; feature 3 agrega decisión por monto y rol. |
