# Plan — Esqueleto del servicio

**Feature 1** · [requirements](requirements.md) · [validation](validation.md) ·
[roadmap](../roadmap.md)

`[compras]` se hace en este repositorio · `[?]` bloqueado por decisión

---

## T1 — Proyecto base

- [x] 1.1 `[compras]` `pom.xml`: Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2, Lombok
      1.18.38, SemVer arrancando en `0.1.0`
- [x] 1.2 `[compras]` Dependencias: `web`, `actuator`, `validation`,
      `spring-cloud-starter-consul-discovery` y OpenFeign
- [x] 1.3 `[compras]` Clase `ComprasApplication`
- [x] 1.4 `[compras]` Estructura hexagonal vacía bajo
      `<agregado>/{domain,application,infrastructure}`; el agregado queda directamente
      bajo el paquete raíz `tesoreria.compras`.
- [x] 1.5 `[compras]` `banner.txt`, como el resto de los servicios
- [x] 1.6 `[compras]` **`jacoco-maven-plugin` con check al 80% que falla el build.**
      Hacerlo ahora, con el proyecto vacío: agregarlo después, con código ya escrito,
      significa arrancar en rojo y tener que ponerse al día

## T2 — Configuración

- [x] 2.1 `[compras]` `bootstrap.yml` con **todo** por variables `APP_*`:
      puerto, nombre de aplicación, host/puerto de Consul, nivel de log
- [x] 2.2 `[compras]` Nombre en Consul: **`tesoreria-compras-service`** — es el nombre
      con el que otros servicios lo descubren
- [x] 2.3 `[compras]` Registro en Consul con `prefer-ip-address: true` y tags
      `tesoreria,compras`, como hacen los demás
- [x] 2.4 `[compras]` Actuator: exponer `health`, y prometheus como core

## T3 — Empaquetado

- [x] 3.1 `[compras]` `Dockerfile` multi-stage copiando el de core:
      `maven:3-eclipse-temurin-25-alpine` para build, `eclipse-temurin:25-jre-alpine` para
      runtime, `curl` instalado (lo usa el healthcheck), usuario no privilegiado
- [x] 3.2 `[compras]` `docker-compose.yml` propio:
      - `name: tesoreria-compras` para no chocar con el stack de core
      - red `tesoreria-shared` como **externa**
      - **sin** Consul ni Kafka: son del compose de core
      - contexto de build `.` (nunca rutas fuera del repo)
- [x] 3.3 `[compras]` Healthcheck sobre `/actuator/health`

## T4 — Llamada real a core

- [x] 4.1 `[compras]` Habilitar Feign y declarar un cliente para
      `tesoreria-core-service`, resuelto por Consul y sin URLs hardcodeadas
- [x] 4.2 `[compras]` `CoreProveedorFeignClientAdapter` que delegue en el cliente Feign y
      llame a `GET /api/tesoreria/core/proveedor/{id}`
- [x] 4.3 `[compras]` Controller de prueba en `/api/tesoreria/compras/ping/{proveedorId}` que devuelva un
      proveedor real traído de core — la prueba de que la cadena entera funciona
- [x] 4.4 `[compras]` `OpenApiConfig` para Swagger, como umhub
- [x] 4.5 `[compras]` Traducir dentro del adapter los errores de Feign a excepciones de
      dominio y responder `404` para proveedor ausente, `503` para core no disponible

## T5 — Verificación

- [x] 5.1 Levantar core + infra, después compras, y comprobar que **conviven** sin chocar
      nombres ni puertos
- [x] 5.2 Ver los dos servicios en la UI de Consul
- [x] 5.3 `GET localhost:8203/api/tesoreria/compras/ping/8` → proveedor real
- [x] 5.4 Correr [validation.md](validation.md) entero

## T6 — Documentación (D12, no opcional)

- [x] 6.1 Caso de uso del ping: actor, precondición, flujo principal, alternativos
- [x] 6.2 Diagrama de secuencia `.mmd`: cliente → compras → Consul → core
- [x] 6.3 README del repo actualizado con cómo levantar y probar el servicio
- [x] 6.4 Los diagramas entran en el **mismo commit** que el código

---

## Orden sugerido

T1, T2, T3 (que levante) → T4 (que hable con core) → T6 → T7.

## Integración posterior

La ruta del gateway se implementa y valida por separado cuando un cliente externo la
necesite. No forma parte de este plan ni del cierre del feature.
