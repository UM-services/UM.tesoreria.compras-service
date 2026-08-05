# Changelog

Formato: [Keep a Changelog](https://keepachangelog.com/es/1.1.0/) ·
Versionado: [SemVer](https://semver.org/lang/es/)

La versión vive en [`VERSION`](VERSION). Cuando exista el `pom.xml`, mantener ambos
sincronizados: `VERSION` es lo que lee el flujo de release, `pom.xml` lo que lee Maven.

---

## [0.1.0] - 2026-08-05

Primer esqueleto ejecutable del servicio.

### Agregado

- Documentación en `specs/`: misión, roadmap y stack técnico, más la carpeta del primer
  feature (esqueleto del servicio) con sus requerimientos, plan y validación.
- Proyecto Maven en Java 25 con Spring Boot, Consul, OpenFeign, Actuator, OpenAPI y
  JaCoCo con cobertura mínima del 80% que falla el build.
- `Dockerfile` multi-stage y `docker-compose.yml` propio para ejecutar el servicio en la
  red compartida.
- Endpoint `GET /api/tesoreria/compras/ping/{proveedorId}`, que consume proveedores de
  core por Feign y Consul.
- Traducción de errores de Feign dentro del adapter: proveedor inexistente responde `404`
  y core no disponible responde `503`.

### Verificado contra el código y la base de `core-service`

- **`OrdenCompra` no existe en core**: ni modelo, ni tabla, ni endpoint. Es la brecha real
  que justifica este servicio.
- **Core está al 70% hexagonal** (1018 de 1460 archivos Java). El 30% restante es legacy,
  más 70 archivos Kotlin.
- **`ProveedorPago` (la orden de pago) no es consumible**: es Kotlin legacy, sin controller
  REST. Bloquea el feature de pagos.
- **La ruta dual de core es un artefacto de su migración**, no una convención: los módulos
  más nuevos usan ruta única. Este servicio usa `/api/tesoreria/compras/...`.
- **Los cargos se modelan como roles explícitos**, no como flags por acción.
- **El gateway no tiene las rutas de compras.** Hay que agregarlas.
- Core no exige `X-API-Key` para proveedores; el contrato usa camelCase y `habilitado`
  numérico.
