# Requirements — Esqueleto del servicio

**Feature 1** del [roadmap](../roadmap.md) · Abierto: 2026-08-04 · Estado: **cerrado**

Global: [misión y reglas](../mission.md) · [stack y convenciones](../tech-stack.md)

---

## Objetivo

Un `compras-service` **vacío pero real**: que levante, se registre en Consul y haga una
llamada verdadera a `core-service`. Sin lógica de negocio todavía.

Es el andamio de todo lo demás. Hasta que exista, ningún feature de dominio se puede
construir ni probar.

## Por qué primero

`compras-service` es cliente de core. Antes de modelar órdenes de compra hay que probar
que el servicio arranca, se descubre por Consul y puede consumir la API de core. Si algo
de eso falla, conviene descubrirlo con un endpoint trivial y no en medio del dominio.

## Molde: `umhub-service` (D11)

Sugerido por el equipo. Son 42 archivos, hexagonal, Spring Boot 4.1.0, Java 25 — el
servicio más chico de la organización.

**Dos diferencias que importan:**

| | umhub | compras |
|---|---|---|
| Persistencia | **no tiene base**; sus puertos de salida son `*ExternalService` | **sí tiene**: es dueño de sus tablas |
| Cliente HTTP | **Feign** (`CampanhaFeignClient`) | **Feign**, para mantener un cliente declarativo y transparente para el equipo |

Para la parte de persistencia el molde correcto es el agregado `Proveedor` de core, no
umhub. Compras conserva sus capas, pero lo ubica como
`tesoreria.compras.proveedor` en vez de copiar el prefijo técnico `hexagonal/compras`.

**Detalles útiles de umhub:**
- Paquete raíz `tesoreria.umhub` (no `um.tesoreria.umhub`, como sí usa core)
- `@RequestMapping("/api/tesoreria/umhub/campanha")` — **ruta única**, confirma D4
- `OpenApiConfig` para Swagger
- `KafkaConsumerConfig` si hace falta consumir eventos
- `ApiKeyFilter` — autenticación **entre servicios** por header `X-API-Key`

## Requerimientos

| ID | Requerimiento |
|---|---|
| REQ-ESQ-01 | Proyecto Maven con Java 25, Spring Boot 4.1.0 y Spring Cloud 2025.1.2, replicando el `pom.xml` de core |
| REQ-ESQ-02 | Layout hexagonal creado, aunque vacío: `domain/model`, `domain/ports/in`, `domain/ports/out`, `application/`, `infrastructure/` |
| REQ-ESQ-03 | `Dockerfile` multi-stage, replicando el de core (build con maven+temurin, runtime con jre-alpine, usuario no privilegiado) |
| REQ-ESQ-04 | `bootstrap.yml` con toda la configuración por variables `APP_*`, sin valores hardcodeados |
| REQ-ESQ-05 | El servicio se registra en Consul como **`tesoreria-compras-service`** — es el nombre con el que los demás servicios lo descubren |
| REQ-ESQ-06 | `docker-compose.yml` propio, uniéndose a `tesoreria-shared` como red **externa**. No duplica Consul ni Kafka: son del compose de core |
| REQ-ESQ-07 | Expone `/actuator/health` para los healthcheck de compose |
| REQ-ESQ-08 | Hace una llamada real a core y devuelve datos reales, no un mock |
| REQ-ESQ-09 | La llamada usa resolución por Consul, **sin URLs hardcodeadas** |
| REQ-ESQ-10 | Controller de prueba con **ruta única**: `GET /api/tesoreria/compras/ping/{proveedorId}` |
| REQ-ESQ-11 | Swagger disponible (`OpenApiConfig`), como umhub |
| REQ-ESQ-12 | El adapter traduce los errores de Feign a excepciones de dominio: proveedor ausente responde `404`; core no disponible responde `503`, nunca `500` |

El cliente de proveedores debe respetar el contrato verificado de core, documentado en
[tech-stack.md](../tech-stack.md#contrato-verificado-de-proveedores). En particular, no
envía `X-API-Key`: core no lo exige.

El detalle de infraestructura de Feign no sale del adapter. Un `FeignException.NotFound`
se traduce a `ProveedorNotFoundException`; cualquier otro error de comunicación o 5xx de
core se traduce a `ProveedorSourceUnavailableException`. La infraestructura web convierte
esas excepciones a `404` y `503`, respectivamente.

## Integración posterior

Cuando un cliente externo necesite acceder por gateway, esa ruta se implementa y valida
como una integración separada en el repositorio del gateway. No es un requerimiento ni
criterio de aceptación de este feature.

## Cliente HTTP elegido

`compras-service` usa **Feign** para consumir `core-service`. Es un cliente declarativo y
`umhub-service` ya ofrece un ejemplo directo para el equipo. La resolución del servicio se
mantiene por Consul, sin URLs hardcodeadas.

`core-service` usa `RestClient`, pero ese patrón no es obligatorio para este servicio.

## Fuera de alcance

- Cualquier modelo de dominio de compras — eso es el feature 2.
- Persistencia y tablas: acá no se crea ninguna.
- Autenticación de usuarios y aplicación de roles. El modelo de roles está decidido, pero
  su integración corresponde al feature de aprobación. La llamada inicial a core no usa
  `X-API-Key`: el `ApiKeyFilter` es propio de umhub y core no tiene ese filtro.

## Bloqueos

Ninguno. **B10 (el puerto) quedó resuelto**: verificado contra el `docker-compose.yml` del
equipo, los puertos ocupados llegan hasta 8202 (guarani), así que **8203 está libre**. Se
avisa al equipo, no hace falta esperar respuesta para arrancar.
