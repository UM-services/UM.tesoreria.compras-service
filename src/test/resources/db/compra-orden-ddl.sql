-- DDL canónico de orden de compra. Este archivo es el único lugar donde vive el SQL.
--
-- Es lo que se le pide al DBA que ejecute y es lo que levantan las pruebas de
-- integración en un MySQL de Testcontainers. Que sea el mismo archivo para las dos
-- cosas es lo que evita que los tests queden verdes contra un esquema que no existe.
--
-- El porqué de cada tabla, campo y tipo está en
-- specs/2026-08-05-orden-de-compra/cambio-base-datos.md.
--
-- La aplicación corre con ddl-auto: none y nunca ejecuta esto por su cuenta.

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
