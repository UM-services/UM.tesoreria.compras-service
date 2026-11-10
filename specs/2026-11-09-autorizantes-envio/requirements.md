# Autorizantes de envío — contexto independiente

## Contexto

El circuito de pedidos de compra necesitaba que un usuario autorizante pudiera aprobar o
rechazar el envío de pedidos de **más de una dependencia**. La primera implementación resolvía
la relación dentro del slice del pedido (`compras/pedidos/compraPedidoAutorizante`) y exponía
una API acoplada al pedido. Esta feature separa esa responsabilidad en un contexto propio de
core y deja a la fachada de compras como consumidora.

## Requerimientos

- **REQ-1.** El vínculo autorizante ↔ dependencias vive en el contexto
  `compras/autorizanteEnvio` de core, independiente de `compras/pedidos/compraPedido`.
- **REQ-2.** El dominio modela el **conjunto**: `AutorizanteEnvio(usuarioId, Set<dependenciaId>)`.
  Un autorizante puede tener cero, una o varias dependencias.
- **REQ-3.** La API permite **reemplazar el conjunto completo** en una sola operación
  (`PUT /usuario/{usuarioId}` con `{ dependenciaIds: [...] }`) y, además, asignar o quitar una
  dependencia puntual (`POST`/`DELETE`).
- **REQ-4.** La persistencia usa una tabla puente propia,
  `compra_autorizante_envio_dependencia`, con PK `(usuario_id, dependencia_id)` y auditoría por
  los listeners de Hibernate.
- **REQ-5.** La migración copia los datos de `compra_pedido_autorizante_dependencia` sin
  eliminarla; la tabla vieja se conserva como respaldo durante una release.
- **REQ-6.** Las escrituras del contexto exigen la clave
  `compras.autorizantes.configurar`; la lectura de un usuario queda disponible para la fachada
  de compras, que valida `compras.enviar_pedido` de su lado.
- **REQ-7.** La fachada de compras conserva su puerto `AutorizanteGateway` y solo cambia el
  cliente Feign al nuevo path.
- **REQ-8.** La pantalla de administración acumula los cambios en un borrador y los persiste
  juntos; el acceso pasa de `administradorGuard` a `permisoGuard('compras.autorizantes.configurar')`.
- **REQ-9.** El path anterior de core (`/api/tesoreria/core/compraPedidoAutorizante`) queda como
  alias deprecado durante una release para permitir el despliegue escalonado.
- **REQ-10.** Documentación y tests: diagrama del contexto, CHANGELOG de los tres repos, spec y
  suites de dominio, casos de uso, mapper y adaptador; spec del componente de administración.

## Fuera de alcance

- Que un **solicitante** pueda crear pedidos para varias dependencias: `usuario.dependenciaId`
  sigue siendo una sola y el contexto del pedido no cambia.
- Co-aprobación: `compra_pedido.autorizante_id` sigue guardando el único usuario que decidió.
- Renombrar o eliminar `compra_pedido_autorizante_dependencia` en esta release.

## Referencias

- `um.tesoreria.core-service/src/main/java/um/tesoreria/core/hexagonal/compras/autorizanteEnvio/`
- `um.tesoreria.core-service/docs/sql/V422__compras_autorizante_envio.sql`
- `um.tesoreria.core-service/docs/hexagonal-autorizanteEnvio.mmd`
- `um.tesoreria.compras-service/src/main/java/tesoreria/compras/slice/pedidoCompra/infrastructure/client/CoreAutorizanteEnvioFeignClient.java`
- `um.tesoreria.frontend-client/libs/feature-administrador/src/lib/autorizantes-envio/`
