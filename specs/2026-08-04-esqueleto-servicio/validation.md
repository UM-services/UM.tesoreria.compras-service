# Validation — Esqueleto del servicio

**Feature 1** · [requirements](requirements.md) · [plan](plan.md)

El feature está terminado cuando pasan **todos** los criterios de acá y las puertas de
calidad del final. No alcanza con que compile.

---

## Criterios

| # | Criterio | REQ | Cómo se verifica |
|---|---|---|---|
| V1 | El servicio levanta y responde | REQ-ESQ-07 | `curl localhost:<puerto>/actuator/health` → `UP` |
| V2 | Aparece en Consul con el nombre exacto | REQ-ESQ-05 | `curl -s localhost:8500/v1/catalog/services` incluye **`tesoreria-compras-service`** |
| V3 | Convive con el stack de core | REQ-ESQ-06 | Los dos compose levantados a la vez, sin choque de nombres ni puertos |
| V4 | **Trae datos reales de core** | REQ-ESQ-08 | `GET /api/tesoreria/compras/ping/8` devuelve el proveedor 8 con los datos reales de core |
| V5 | Sin URLs hardcodeadas | REQ-ESQ-09 | `grep -rn "localhost:8092\|http://tesoreria-core" src/` → vacío |
| V6 | Ruta única canónica | D4 | El controller usa `@RequestMapping("/api/tesoreria/compras/...")`, sin forma dual `{...}` |
| V7 | Swagger disponible | REQ-ESQ-11 | `/swagger-ui` responde |
| V8 | Configuración por variables | REQ-ESQ-04 | Cambiar `APP_PORT` cambia el puerto sin tocar código |
| V9 | Es repetible | — | `docker compose down && docker compose up -d` vuelve a dar V1 y V4 |
| V10 | El compose es portable | D7 | Sin rutas absolutas ni contextos fuera del repo; sobrevive copiado a un worktree |
| V11 | Errores distinguibles para el consumidor | REQ-ESQ-12 | proveedor inexistente → `404`; core no disponible → `503`; ninguno responde `500` |

**V4 es el criterio principal.** El resto puede pasar con un servicio que en realidad no
habla con nadie. V4 prueba que la cadena compras → Consul → core → base funciona.

## Cómo se ve el fallo

| Síntoma | Causa probable |
|---|---|
| V2 falla | El nombre de aplicación no coincide, o Consul no está levantado |
| V4 da 503 o vacío | Core no puede alcanzar la base, o el cliente Feign no encuentra el servicio. Verificar la conectividad TCP configurada para el entorno y el registro en Consul |

---

## Puertas de calidad

Aplican a todo merge. Si alguna falla, no se mergea.

### Arquitectura

| # | Puerta | Cómo se verifica |
|---|---|---|
| A1 | El modelo de dominio no importa frameworks | `grep -r "^import" src/main/java/**/domain/model/` sólo `java.*`, `lombok.*` y otros modelos. Cero Spring, JPA, Jackson |
| A2 | Las dependencias apuntan hacia adentro | `domain/` no importa nada de `application/` ni `infrastructure/` |
| A3 | Inyección por constructor | `grep -rn "@Autowired" src/main` → **cero** |
| A4 | Ruta única | `grep -rn "@RequestMapping({" src/main` → **cero** |
| A5 | Estructura hexagonal presente | Existen `domain/model`, `domain/ports/in`, `domain/ports/out`, `application/`, `infrastructure/` |

En este feature todavía no hay dominio real, así que A1 y A2 se verifican sobre lo que
haya. Importa dejar la estructura correcta desde el principio: es mucho más caro
enderezarla después.

### Tests

| # | Puerta |
|---|---|
| T1 | El `Consumer` que llama a core tiene test, mockeando el cliente HTTP |
| T2 | Los tests corren **sin** base de datos ni red |
| T3 | Estilo de core: JUnit 5 + Mockito + AssertJ |
| T4 | **Cobertura ≥ 80%**, verificada por JaCoCo, con el check configurado para **fallar el build** si baja |
| T5 | JaCoCo queda configurado en este feature — es el momento barato de hacerlo, antes de que haya código que cubrir |

### Documentación (D12 — *"indispensable"*)

| # | Puerta | REQ |
|---|---|---|
| D1 | Caso de uso escrito del flujo de ping | REQ-DOC-01 |
| D2 | Diagrama de secuencia `.mmd`: cliente → compras → Consul → core → base | REQ-DOC-02 |
| D3 | README actualizado con cómo levantar y probar | — |
| D4 | Los diagramas entraron en el **mismo commit** que el código | REQ-DOC-04 |

### Operación

| # | Puerta |
|---|---|
| O1 | `docker compose up -d` levanta el servicio sano desde cero |
| O2 | El compose no duplica Consul ni Kafka |
| O3 | Versión SemVer en `pom.xml` (`0.1.0`) |

---

## Terminado cuando

1. V1 a V11 pasan.
2. Las puertas de calidad pasan.
3. `REQ-ESQ-01..11` y `REQ-ESQ-12` están cubiertos.
4. El puerto 8096 fue comunicado al equipo al incorporarlo al compose compartido.

## Integración (fuera del alcance del feature)

Cuando el frontend necesite llegar a compras por gateway, se agrega la ruta de compras en
el repositorio del gateway y se valida ese recorrido. No condiciona el cierre de este
feature.
