-- Copia literal del DDL de specs/2026-08-05-orden-de-compra/cambio-base-datos.md.
-- Si el DBA aplica algo distinto, este archivo debe seguirlo: es lo que valida que
-- las entidades JPA y el SQL de la secuencia calzan con el esquema real.
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
