# Cambio de base — Orden de compra

**Estado:** propuesta para consenso y aplicación por DBA. La aplicación mantiene
`ddl-auto: none` y no ejecuta este DDL.

## Tablas solicitadas

```sql
CREATE TABLE compra_orden_secuencia (
  anio SMALLINT NOT NULL PRIMARY KEY,
  ultimo_numero BIGINT NOT NULL
);
CREATE TABLE compra_orden (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  numero VARCHAR(16) NOT NULL UNIQUE,
  fecha_emision DATE NOT NULL,
  proveedor_id INT NOT NULL,
  sede_id INT NOT NULL,
  observaciones VARCHAR(1000) NULL,
  estado VARCHAR(32) NOT NULL,
  total DECIMAL(19,2) NOT NULL,
  INDEX ix_compra_orden_consulta (estado, proveedor_id, sede_id, fecha_emision)
);
CREATE TABLE compra_orden_item (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  orden_compra_id BIGINT NOT NULL,
  articulo_id INT NOT NULL,
  descripcion VARCHAR(255) NULL,
  cantidad DECIMAL(19,4) NOT NULL,
  precio_unitario DECIMAL(19,2) NOT NULL,
  imputacion_id BIGINT NOT NULL,
  CONSTRAINT fk_compra_orden_item_orden FOREIGN KEY (orden_compra_id) REFERENCES compra_orden(id)
);
```

`compra_orden_secuencia` guarda el correlativo anual. La reserva usa el incremento atómico
de MySQL con `LAST_INSERT_ID`, por lo que altas concurrentes no repiten un número.
