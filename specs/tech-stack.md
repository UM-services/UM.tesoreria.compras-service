# Stack Tecnológico

> Verificado contra el código de `core-service` el 2026-08-04 (`pom.xml`, `Dockerfile`,
> `bootstrap.yml`, y el árbol de `src/`) y contra el `docker-compose.yml` compartido del
> equipo. Cada afirmación acá se comprobó ejecutándola contra el código, no de memoria.
> Lo no confirmado está marcado como **A CONFIRMAR**.

## Lenguaje y framework

`compras-service` debe replicar a `core-service` para que el equipo tenga un solo stack,
no dos.

| | Versión | Nota |
|---|---|---|
| Java | **25** | `<java.version>25</java.version>` |
| Spring Boot | **4.1.0** | vía `spring-boot-starter-parent` |
| Spring Cloud | **2025.1.2** | |
| Maven | 3.x | core no tiene wrapper; usa `maven:3-eclipse-temurin-25-alpine` en el build |
| Lombok | 1.18.38 | `@RequiredArgsConstructor` + inyección por constructor es el estilo de la casa |
| Kotlin | 2.4.10 | la propiedad está declarada, pero el código está **migrando activamente** de Kotlin a Java — escribir código nuevo en Java |

## Arquitectura

**Hexagonal (puertos y adaptadores)**. En core, el 70% de esta arquitectura vive bajo
`hexagonal/` (no todo; ver la sección sobre el 30% legacy más abajo). Compras conserva
la misma separación de responsabilidades, pero ubica cada agregado directamente bajo
`tesoreria.compras`: el nombre del paquete expresa el dominio, no el estilo de
arquitectura.

```
<agregado>/
├── domain/
│   ├── model/
│   │   ├── Proveedor.java              # SIN FRAMEWORKS: sólo java.* + lombok + otros modelos de dominio
│   │   └── ProveedorSearch.java        # modelo aparte para proyecciones de búsqueda
│   └── ports/                          # OJO: "ports" en plural, y adentro in/ y out/
│       ├── in/                         # una interfaz POR OPERACIÓN, un método cada una
│       │   ├── GetProveedorByIdUseCase.java
│       │   ├── CreateProveedorUseCase.java
│       │   └── … (9 para Proveedor)
│       └── out/
│           └── ProveedorRepository.java   # el único contrato de persistencia que el dominio conoce
├── application/
│   ├── usecases/
│   │   └── GetProveedorByIdUseCaseImpl.java   # @Component @RequiredArgsConstructor
│   ├── service/
│   │   └── ProveedorService.java              # fachada @Service, inyecta TODOS los casos de uso
│   └── exception/
│       └── ProveedorException.java
└── infrastructure/
    ├── persistence/
    │   ├── entity/ProveedorEntity.java            # JPA vive SÓLO acá
    │   ├── repository/JpaProveedorRepository.java # interfaz Spring Data
    │   ├── adapter/JpaProveedorRepositoryAdapter.java  # implementa el puerto de salida
    │   └── mapper/ProveedorMapper.java            # entity ↔ dominio
    └── web/
        ├── controller/ProveedorController.java    # @RestController
        ├── dto/ProveedorRequest.java, ProveedorResponse.java
        └── mapper/ProveedorDtoMapper.java         # dominio ↔ dto
```

### Las reglas que esto codifica

- **Las dependencias apuntan sólo hacia adentro.** `infrastructure → application → domain`.
- **Los modelos de dominio no importan frameworks.** Verificado: el `Proveedor` de core
  importa únicamente `java.math.BigDecimal`, `lombok.*` y otro modelo de dominio. Nada más
  está permitido.
- **Las implementaciones de caso de uso dependen de puertos de salida, nunca de
  repositorios Spring Data.**
- **La fachada `@Service` compone casos de uso; no contiene lógica.** El
  `ProveedorService` de core son nueve delegaciones de una línea.
- **Dos mappers, ambos obligatorios.** La entity nunca llega al controller; el DTO nunca
  llega al dominio.

### Fragmentos de referencia verificados

```java
// domain/ports/in — una operación, una interfaz
public interface GetProveedorByIdUseCase {
    Optional<Proveedor> getProveedorById(Integer proveedorId);
}

// application/usecases — @Component, inyección por constructor, depende del puerto de SALIDA
@Component
@RequiredArgsConstructor
public class GetProveedorByIdUseCaseImpl implements GetProveedorByIdUseCase {
    private final ProveedorRepository repository;

    @Override
    public Optional<Proveedor> getProveedorById(Integer proveedorId) {
        return repository.findByProveedorId(proveedorId);
    }
}

// infrastructure/web/controller
// OJO: esta ruta dual es de core y NO se copia — ver la sección siguiente.
// En compras va ruta única: @RequestMapping("/api/tesoreria/compras/ordenCompra")
@RestController
@RequestMapping({"/proveedor", "/api/tesoreria/core/proveedor"})
@RequiredArgsConstructor
public class ProveedorController {
    private final ProveedorService proveedorService;
    private final ProveedorDtoMapper proveedorDtoMapper;
    // … devuelve ResponseEntity.ok(...)
}
```

### La ruta dual NO es la convención a copiar ⚠️

Verificado: de los **40 controllers hexagonales, sólo 25 usan ruta dual**. Los 15
restantes se parten en dos grupos, y la diferencia importa:

- **Módulos viejos** — sólo ruta corta: `/contrato`, `/documento`, `/clasechequera`,
  `/aranceltipo`, `/chequeratotal`.
- **Módulos nuevos** — sólo ruta canónica: `/api/tesoreria/core/guaraniUbicacion`,
  `/api/tesoreria/core/umhub/campanha`, `/api/tesoreria/core/politicaArancelaria`,
  `/api/tesoreria/core/auth`, `/api/tesoreria/core/mercadoPagoContext`.

`GuaraniUbicacion` es el módulo **más nuevo de core** (agregado en 3.47.0 según el README)
y usa **ruta única canónica**. O sea: la ruta dual es un **artefacto de la migración**
—mantener viva la URL vieja mientras se migra— y no un patrón a imitar en un servicio
nuevo que no tiene URLs viejas que preservar.

**Para `compras-service`, ruta única:**

```java
@RestController
@RequestMapping("/api/tesoreria/compras/ordenCompra")
```

Confirmarlo con el equipo, pero la evidencia del código apunta claramente ahí.

Contextos existentes en core: `auth`, `chequera`, **`compras`**, `comprobante`,
`contable`, `contrato`, `documento`, `extern`, `facultad`, `guarani`, `lectivo`,
`persona`, `ubicacion`, `usuario`, entre otros.

## Testing

Core tiene 17 tests con un estilo consistente, que replica los paquetes hexagonales:

```java
@ExtendWith(MockitoExtension.class)
class AddLectivoTotalImputacionUseCaseImplTest {
    @Mock private LectivoTotalImputacionRepository repository;
    @InjectMocks private AddLectivoTotalImputacionUseCaseImpl useCase;
    // JUnit 5 + Mockito + AssertJ (assertThat)
}
```

- Los tests viven en `src/test/java/…/<mismo paquete>/`, nombrados `<ClaseBajoPrueba>Test`.
- Cubren implementaciones de caso de uso, services y mappers de DTO.
- Mockean el **puerto de salida**, nunca un repositorio JPA concreto — así el test valida
  el caso de uso aislado, que es justamente lo que la arquitectura hexagonal habilita.

Replicar esto. Es una de las cosas que el equipo mira.

### Cobertura mínima: 80%, siempre

**La cobertura de tests unitarios no baja de 80% en ningún momento.** No es una meta para
el final: es una condición que se sostiene commit a commit.

- Se mide con **JaCoCo**, que core ya usa (`sonar.coverage.jacoco.xmlReportPaths` en su
  `pom.xml`, reportando a SonarCloud).
- Configurar el `jacoco-maven-plugin` con una **regla de check al 80% de líneas** que
  **falle el build** si no se cumple. Un umbral que no rompe nada no es un umbral.
- Se excluyen del cómputo: DTOs, entities JPA y la clase `*Application` — son estructura
  sin lógica y sólo inflan el número.
- Lo que **sí** tiene que estar cubierto: casos de uso, lógica de dominio (sobre todo las
  transiciones de estado) y mappers.

La arquitectura hexagonal hace esto barato: los casos de uso se testean mockeando el
puerto de salida, sin base de datos ni red. Si llegar al 80% cuesta, casi siempre es señal
de que hay lógica en el lugar equivocado — en el controller o en el adapter, en vez del
dominio.

## Comunicación entre servicios

- **Service discovery:** HashiCorp **Consul**
  (`spring-cloud-starter-consul-discovery`), registrado con `prefer-ip-address: true` y
  tags. Consul es **obligatorio al arrancar** — el servicio no levanta sin él.
- **HTTP entre servicios:** `compras-service` usa **Feign** como cliente declarativo y
  resuelve servicios hermanos por Consul, sin URLs hardcodeadas. `core-service` usa
  `RestClient` con el patrón `*UrlResolver` + `*Consumer`; es una referencia válida, pero
  no una convención obligatoria para compras.
- **API de detalle:** un `GET` de recurso debe devolver los datos necesarios para mostrar
  ese recurso en una sola respuesta. En particular, el detalle de una orden de compra no
  obliga al cliente a encadenar llamadas para completar sus datos.
- **Asincrónico:** **Kafka** (`spring-kafka`). Core escucha en `tesoreria-core-group`.
  También es obligatorio al arrancar con la configuración actual.
- **Gateway:** `tesoreria-gateway-service` en el `8301` está delante de los servicios de
  tesorería, para los clientes **externos**. Este servicio **no lo necesita**: resuelve
  core por Consul directamente y se valida en su propio puerto (8203). El ruteo por
  gateway es integración — ver el roadmap.

## Datos

- **MySQL** (`mysql-connector-j` 9.7.0), esquema `tesium`, Hibernate `ddl-auto: none`
  — **el esquema se administra fuera de la aplicación; nunca dejar que JPA lo altere.**
- `open-in-view: false`; pool HikariCP máximo 100.
- **`compras-service` es dueño de su propia persistencia** para el dominio de órdenes de
  compra (`OrdenCompra`, niveles de aprobación, centro de costos). Se construyen acá, **no**
  se agregan a core.
- **No lee las tablas de core directamente.** Proveedores, artículos, facturas y
  contabilidad se alcanzan por la API REST de core. Dos servicios nunca escriben la misma
  tabla.
- Abierto: si las tablas de compras van en un esquema separado o junto a `tesium`
  — ver pregunta abierta 1 del roadmap.

> **Sin estrategia de migraciones todavía.** Core no usa Flyway ni Liquibase, y
> `ddl-auto` está en `none`: alguien crea las tablas a mano. Como compras ahora es dueño
> de sus tablas, hay que definir **quién las crea y con qué herramienta** antes de escribir
> la primera línea de persistencia.

## Qué provee core ya (reusar, no reconstruir)

Confirmado en `hexagonal/compras`, `hexagonal/comprobante`, `hexagonal/contable`:

| Agregado | Notas |
|---|---|
| `Proveedor` | cuit, razón social, cbu, cuenta contable, habilitado |
| `Articulo` | maestro de artículos (+ `ArticuloSearch`) |
| `ProveedorMovimiento` | **este es el registro de la factura** — incluye `fechaVencimiento`, `neto`, `importe`, `cancelado`, `concepto` y una lista de `ordenPagos` |
| `ProveedorPago` | ⚠️ **la orden de pago, pero NO es un activo hexagonal** — ver abajo |
| `FacturaPendiente` | vista de facturas pendientes, ya trae `fechaVencimiento` |
| `Comprobante` | tipos de comprobante |
| `Cuenta`, `CuentaMovimiento`, `Asiento` | contabilidad |
| `LectivoTotalImputacion` | totales de imputación |

### ⚠️ Core NO está 100% hexagonal — y `ProveedorPago` lo demuestra

Dato verificado: de **1460 archivos Java, 1018 están en `hexagonal/`** (70%). El 30%
restante es legacy: `controller/` (69), `service/` (106), `repository/` (85), `model/`
(74), `exception/` (63). Además quedan **70 archivos Kotlin**.

`ProveedorPago` —la orden de pago, pieza central del Grupo 7— está en ese 30%:

```
kotlin/model/ProveedorPago.kt          @Entity, tabla movprov_detallecomprobantes
repository/ProveedorPagoRepository.java
service/ProveedorPagoService.java
(ningún controller)                    ← NO tiene endpoint REST
```

Consecuencias concretas:

1. **Hoy `compras-service` no puede consumir la orden de pago por REST.** No hay endpoint.
2. Es **Kotlin**, justo el lenguaje del que core se está yendo.
3. Planificábamos "reusar `ProveedorPago` de core". Ese supuesto **no se sostiene sin un
   cambio en core** — migrarlo a hexagonal y exponerlo. Y "no tocar core" era una de
   nuestras reglas.

Antes del Grupo 7 hay que decidir con el equipo: ¿se migra `ProveedorPago` a hexagonal
(trabajo en core), o compras modela su propia orden de pago? Es una pregunta de frontera,
no un detalle.

Regla general que se desprende: **antes de dar por reusable un activo de core, verificar
que esté en `hexagonal/` y que tenga controller.** Estar en la base no significa estar
disponible por API.

**Ausente en core — ésta es la brecha:**

- `OrdenCompra` — sin modelo, sin tabla referenciada, sin endpoint. Lo central a construir.
- Niveles de aprobación / autorización por monto.
- `CentroCosto`, y la jerarquía de imputación Sede → Unidad Académica → Carrera.
- Bases de asignación (división automática de facturas).
- Márgenes de tolerancia entre OC y factura.

### Contrato verificado de proveedores

Verificado contra `core-service`: no existe un filtro `OncePerRequestFilter`,
`ApiKeyFilter` ni manejo de `X-API-Key`; los endpoints de proveedores responden igual con
o sin ese header. `ApiKeyFilter` pertenece a `umhub-service`, no a core, por lo que
`compras-service` no debe exigir ni enviar una API key para este consumo.

El contrato de proveedores es consistente en detalle, listado y paginado:

- Usa propiedades **camelCase**.
- `habilitado` es numérico (`0` o `1`), no booleano.
- Incluye `cuenta` como objeto contable anidado completo.
- Los strings ausentes se representan como `""`, no como `null`.
- La respuesta paginada tiene la forma
  `{data, totalElements, totalPages, currentPage, pageSize}`.

> Nota: el documento fuente pide *agregar* `fechaVencimiento` a la planilla de pagos —
> pero el campo ya existe tanto en `ProveedorMovimiento` como en `FacturaPendiente`.
> Conviene confirmar con el equipo si la brecha real es el campo, o que no se carga /
> no se muestra en la UI.

## Seguridad y autorización — integración pendiente

Punto crítico verificado en el código:

- `core-service` **no tiene la dependencia `spring-boot-starter-security`**
- **no hay `SecurityFilterChain`, ni `@PreAuthorize`, ni `WebSecurity`** en ninguna parte
- su contexto `auth` es sólo login: `LoginUseCase`, `UsuarioAuth`, `AuthController`

Es decir: **no hay control de roles a nivel de endpoint en el servicio de referencia.**
Presumiblemente ocurre en `tesoreria-gateway-service`, o directamente no ocurre.

Decisión vigente: los cargos se modelan como **roles explícitos**, no como flags por
acción. Antes de construir aprobaciones hay que definir el catálogo y las asignaciones de
roles, y confirmar cómo llegan identidad y roles al servicio.

## Desarrollo local

### Topología

Dos repositorios independientes, una red Docker compartida:

```
        ┌─────────────── tesoreria-shared (red externa) ────────────────────┐
        │                                                                    │
   consul-service:8500   kafka:9092   tesoreria-core-service:8092   compras-service
        │                                                                    │
        └──────────────────────────────┬─────────────────────────────────────┘
                                       │
                                  base de desarrollo
```

- **Un `docker-compose.yml` por repositorio**, commiteado dentro de ese repositorio — *no*
  un archivo compartido en una carpeta padre. Así cada servicio se puede levantar desde su
  propio checkout y no depende de rutas relativas a otros repositorios.
- La red se crea una vez, a mano, fuera de ambos compose:
  `docker network create tesoreria-shared`
- `COMPOSE_PROJECT_NAME` distinto por repositorio para evitar colisiones de nombres.

### Acceso a la base de datos

La base de desarrollo se alcanza a través de la red privada configurada por el equipo.
Antes de levantar los contenedores, cada desarrollador debe comprobar que su motor de
contenedores puede abrir una conexión TCP a la base; un ping no valida esa conectividad.

La configuración de acceso (host, credenciales, túneles o relays) es local y no debe
versionarse en este documento ni en archivos de configuración compartidos. Debe proveerse
mediante las variables o archivos ignorados definidos por el repositorio.

### Trabajo en paralelo

Los worktrees aíslan el estado de Git, pero no los puertos, los nombres de contenedor ni
el demonio de contenedores. Regla práctica:

- En una misma máquina, mantener **un solo** stack de `core-service` corriendo (es dueño
  de Consul + Kafka + core).
- Los worktrees de `compras-service` de esa máquina pueden conectarse a ese stack.
- No correr dos stacks del *mismo* repositorio a la vez; colisionan en los puertos.

Los scripts de setup por repositorio deben asegurar que la red compartida exista y cargar
la configuración ignorada por Git. No deben depender de una aplicación de agentes o de un
sistema operativo concreto.

## Puertos

Tomados del compose del equipo. Para `compras-service` se reserva el **8203**, siguiente
puerto libre luego de `guarani:8202`; avisar al equipo al incorporarlo al compose compartido.

| Servicio | Puerto |
|---|---|
| consul | 8500 |
| tesoreria-gateway | 8301 |
| **tesoreria-core** | **8092** |
| report | 8281 |
| chequera-proxy | 8121 |
| facturador | 8097 |
| mercadopago | 8099 |
| sender | 8188 |
| umhub | 8201 |
| guarani | 8202 |
| kafka | 9092 interno / 29092 host |
| frontends (incl. compras-client) | 4201–4209 |

> **Ojo:** el compose del equipo ya construye `tesoreria-compras-client` (puerto 4201)
> desde `um.tesoreria.frontend-client`, `apps/compras/Dockerfile`, con
> `BACKEND_URL=/api/tesoreria`. **Ya existe un frontend de compras.** Hay que revisar qué
> API espera antes de diseñar endpoints — ver la revisión, hallazgo C2.

## Convenciones que conviene copiar de core

- Inyección por constructor con `@RequiredArgsConstructor`; core migró activamente para
  sacarse `@Autowired`, no reintroducirlo.
- Los controllers devuelven `ResponseEntity.ok(...)`.
- Excepciones propias por agregado (`<Agregado>Exception`).
- DTOs separados de los modelos de dominio, con `*DtoMapper` explícito.
- SemVer en `pom.xml`, con su entrada en `CHANGELOG.md` / README por release.
- Actuator expuesto, métricas Prometheus habilitadas, health check en
  `/actuator/health` (lo usan los healthcheck de compose).

## Flujo de ramas

Las ramas de referencia son `main`, `develop` y `staging`. La rama de trabajo actual es
`dino`. La promoción de cambios siempre sigue este orden:

```
dino → develop → staging → main
```

- Los cambios se desarrollan y validan primero en `dino`.
- `develop` integra los cambios listos para desarrollo conjunto.
- `staging` recibe únicamente lo validado en `develop` para pruebas de preproducción.
- `main` recibe únicamente lo aprobado en `staging`; no se hacen cambios directos en
  `staging` ni en `main`.

En la inicialización del repositorio, `main`, `develop` y `staging` parten de un commit
vacío. El primer contenido del servicio se incorpora en `dino` y se promociona siguiendo
este flujo.
