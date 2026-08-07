# Cambio de base — Orden de compra

**Estado:** propuesta para consenso y aplicación por DBA.

**El DDL vive en un solo lugar:
[`src/test/resources/db/compra-orden-ddl.sql`](../../src/test/resources/db/compra-orden-ddl.sql).**

Ese archivo es el que se ejecuta y el que levantan las pruebas de integración contra un
MySQL real. No está copiado acá a propósito: dos copias del mismo `CREATE TABLE` se
desincronizan y los tests quedan verdes contra un esquema que no es el de producción.

Este documento explica lo que el `.sql` no puede: qué se pide, para qué caso de uso y por
qué esos campos y esos tipos. Al DBA se le mandan los dos juntos.

La aplicación mantiene `ddl-auto: none` y no crea ni modifica el esquema en ningún momento.

---

## Qué se pide y para qué

### `compra_orden` — la cabecera

Es el agregado que autoriza el gasto antes de que exista una factura. Lo necesitan
[UC-OC-01](use-case-orden-compra.md) (alta), UC-OC-02 (consulta y listado) y UC-OC-03
(transiciones de estado).

| Campo | Por qué |
|---|---|
| `numero VARCHAR(16) UNIQUE` | El identificador público `OC-AAAA-NNNNNN` (decisión B11). 14 caracteres hoy; 16 deja margen sin ser holgado. El `UNIQUE` es la última línea de defensa contra un número repetido, además de la reserva atómica. |
| `fecha_emision DATE` | De acá sale el año del correlativo, y queda inmutable junto con el número. |
| `proveedor_id INT`, `sede_id INT` | Referencias a `core-service`, **sin FK**: son otra base y no queremos acoplar el esquema (REQ-OC-02). |
| `estado VARCHAR(32)` | La máquina de estados vive en el dominio, no en la base. `VARCHAR` en vez de `ENUM` para que agregar un estado no requiera un `ALTER TABLE` ni un cambio coordinado con el DBA. |
| `total DECIMAL(19,2)` | Importes en decimal exacto, nunca `FLOAT` ni `DOUBLE`. Es un valor derivado de los ítems, guardado para que reportes y BI no tengan que sumar el detalle; la fuente de verdad siguen siendo los ítems y la aplicación verifica que coincidan al leer. |
| `INDEX ix_compra_orden_consulta` | Cubre el listado de REQ-OC-09, que combina estado, proveedor, sede y rango de fecha. En ese orden porque el estado es el filtro más selectivo del circuito. |

No hay borrado físico: la baja es la transición a `ANULADA` (REQ-OC-11), así que no se
necesita ninguna columna de borrado lógico.

### `compra_orden_item` — el detalle

Un renglón por artículo, con su imputación. Los ítems no tienen vida propia fuera de su
orden, por eso la FK a `compra_orden` sí existe: es la única relación dentro de compras.

| Campo | Por qué |
|---|---|
| `cantidad DECIMAL(19,4)` | Cuatro decimales para unidades fraccionadas; el importe redondea a dos, la cantidad no. |
| `precio_unitario DECIMAL(19,2)` | Decimal exacto, igual que el total. |
| `articulo_id INT`, `imputacion_id BIGINT` | Referencias externas sin FK, misma razón que en la cabecera. |

El `id` importa: la aplicación lo conserva entre cambios de estado en lugar de borrar y
reinsertar el detalle, para que lo que se construya encima (facturas parciales, feature 3)
pueda referenciar un renglón de forma estable.

### `compra_orden_secuencia` — el correlativo

Una fila por año: `anio` es la PK y `ultimo_numero` el último entregado. Reinicia cada año
calendario y es global al servicio, no por sede (decisión B11).

La reserva es una sola sentencia atómica:

```sql
INSERT INTO compra_orden_secuencia (anio, ultimo_numero)
VALUES (?, LAST_INSERT_ID(1))
ON DUPLICATE KEY UPDATE ultimo_numero = LAST_INSERT_ID(ultimo_numero + 1);
```

`LAST_INSERT_ID(expr)` guarda el valor en la sesión y lo devuelve el `SELECT LAST_INSERT_ID()`
siguiente, así que dos altas concurrentes nunca reciben el mismo número sin necesidad de un
`SELECT ... FOR UPDATE` ni de una tabla bloqueada. Como el valor es **por sesión de MySQL**,
las dos sentencias tienen que correr sobre la misma conexión: eso lo garantiza la transacción
del alta, que además hace que un alta fallida devuelva el número en lugar de dejar un hueco
en la serie.

Verificado con 24 altas concurrentes en `OrdenCompraPersistenciaIT`.

---

## Qué necesitamos del DBA

1. Revisar y consensuar el esquema (ítem T1.3 del [plan](plan.md), todavía abierto).
2. Definir en qué base o esquema viven estas tablas dentro de `tesium`.
3. Aplicar el `.sql` tal cual, o devolvernos la versión ajustada.

**Si el esquema aplicado termina siendo distinto, hay que actualizar
`compra-orden-ddl.sql`**: las pruebas de integración corren contra ese archivo, y si se
aparta del esquema real dejan de probar nada útil.
