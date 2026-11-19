# Plan — Autorizantes de envío (contexto independiente)

## Fases

### F1 — Core: dominio y aplicación

- [x] Agregado `AutorizanteEnvio` con `usuarioId` y `Set<dependenciaId>`.
- [x] Puertos de entrada `Get`, `Reemplazar`, `Asignar`, `Quitar`, `List`.
- [x] Puerto de salida `AutorizanteEnvioRepository`.
- [x] Casos de uso y fachada `AutorizanteEnvioService`.
- [x] Excepción `AutorizanteEnvioException`.

### F2 — Core: persistencia y API

- [x] Entidad `AutorizanteEnvioDependenciaEntity` (`@IdClass`, `Auditable`) sobre
  `compra_autorizante_envio_dependencia`.
- [x] Repositorio derivado y adaptador con diff de altas/bajas.
- [x] DTOs, mapper y controller en `/api/tesoreria/core/compras/autorizanteEnvio`.
- [x] Alias deprecado `/api/tesoreria/core/compraPedidoAutorizante`.
- [x] `@RequierePermiso("compras.autorizantes.configurar")` en las escrituras.
- [x] Clave en `permisos.yaml` y script `V422__compras_autorizante_envio.sql`.

### F3 — Compras: consumo

- [x] `CoreAutorizanteEnvioFeignClient` y `CoreAutorizanteEnvioResponse`.
- [x] Adaptador `CoreAutorizanteEnvioFeignClientAdapter` implementando `AutorizanteGateway`.
- [x] Retiro de los DTOs/Feign anteriores.
- [x] Test del adaptador.

### F4 — Frontend: administración

- [x] URLs al nuevo contexto y `AutorizanteDependencias.usuarioId`.
- [x] Borrador local + `PUT` del conjunto completo + `Descartar`.
- [x] Guard por `compras.autorizantes.configurar` y menú con `permiso`.
- [x] Spec del componente (carga, guardado múltiple, sin cambios, descarte).

### F5 — Documentación y despliegue

- [x] Diagrama `hexagonal-autorizanteEnvio.mmd` y registro en el visor.
- [x] `AGENTS.md`, `docs/README.md`, CHANGELOG de core/compras/frontend.
- [x] Spec y actualización del plan raíz.
- [ ] Aplicar `V422` en desarrollo/staging/producción.
- [ ] Retirar el alias y la tabla vieja en la release siguiente.

## Decisiones

- **Tabla nueva** en lugar de reutilizar la anterior: el nombre deja de estar atado al pedido y
  la migración copia los datos.
- **`PUT` del conjunto** como operación principal: evita estados parciales y simplifica la UI.
- **Alias temporal** en web, no en dominio: permite desplegar core antes que compras y frontend.
- **IDs solamente** en el dominio y la API: no se duplican datos de `usuario` ni `dependencia`;
  la pantalla resuelve los nombres con los catálogos existentes.
