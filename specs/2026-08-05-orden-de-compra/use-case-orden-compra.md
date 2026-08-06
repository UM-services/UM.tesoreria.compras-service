# Casos de uso — Orden de compra

## UC-OC-01 — Crear orden de compra

**Actor:** Director de Compras (la autorización por rol se incorpora en feature 3).

**Precondiciones:** existen los datos mínimos de cabecera y al menos un ítem con su
imputación. Los IDs externos se reciben como referencias y no se validan contra core.

**Flujo principal:**

1. El actor envía la cabecera, ítems e imputaciones.
2. Compras valida la estructura y calcula el total.
3. Reserva el próximo correlativo anual global.
4. Persiste la orden en `PENDIENTE_DE_APROBAR`.
5. Devuelve el detalle completo y su número `OC-AAAA-NNNNNN`.

**Alternativos:**

- Sin ítems, cantidades/importes inválidos o imputación ausente: se rechaza la solicitud.
- Conflicto al reservar el número: la transacción se reintenta/falla sin crear una orden
  parcialmente persistida.

## UC-OC-02 — Consultar y listar

**Actor:** consumidor autorizado de la API.

**Precondiciones:** ninguna para listar; la orden existe para detalle.

**Flujo principal:**

1. El actor solicita una orden por id/número o un listado con filtros.
2. Compras obtiene su propia persistencia.
3. Para detalle carga cabecera, ítems e imputaciones.
4. Devuelve una sola respuesta sin consultar core.

**Alternativos:** orden inexistente devuelve 404; filtros sin coincidencias devuelven una
lista vacía.

## UC-OC-03 — Transicionar estado

**Actor:** Director de Compras; las restricciones por rol y monto se aplican en feature 3.

**Precondiciones:** la orden existe y la transición solicitada está permitida desde su
estado actual.

**Flujo principal:**

1. El actor invoca una acción explícita (`aprobar`, `enviar`, cumplimiento parcial/total,
   factura parcial o `anular`).
2. El caso de uso delega la transición al agregado.
3. El agregado valida el grafo de estados y persiste el nuevo estado.
4. Se devuelve la orden actualizada.

**Alternativos:** una transición inválida se rechaza sin modificar la orden. No hay una
actualización genérica que acepte un estado en el cuerpo de la solicitud.
