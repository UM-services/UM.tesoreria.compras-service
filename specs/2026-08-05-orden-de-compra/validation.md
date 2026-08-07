# Validation — Orden de compra

**Feature 2** · [requirements](requirements.md) · [plan](plan.md)

El feature queda cerrado sólo cuando pasan estos criterios y las puertas de calidad.

---

## Criterios funcionales

| # | Criterio | REQ | Cómo se verifica |
|---|---|---|---|
| V1 | Alta numerada | REQ-OC-01, 04 | Crear dos órdenes secuenciales del mismo año produce números consecutivos `OC-AAAA-NNNNNN` y estado `PENDIENTE_DE_APROBAR`; varias altas concurrentes generan números únicos. |
| V2 | Total confiable | REQ-OC-03 | Un total enviado por el cliente se ignora/rechaza; el resultado equivale a la suma de ítems con `BigDecimal`. |
| V3 | Sin dependencia de core | REQ-OC-02 | Crear/editar con IDs de proveedor, artículo e imputación no requiere red ni consulta a core. |
| V4 | Detalle completo | REQ-OC-08 | `GET` por id y por número devuelve cabecera, estado, total, ítems e imputaciones en una respuesta. |
| V5 | Filtros | REQ-OC-09 | El listado filtra correctamente por cada parámetro y por combinaciones de estado, proveedor, sede y rango inclusivo. |
| V11 | Listado acotado | REQ-OC-09 | El listado es siempre paginado: sin parámetros devuelve la primera página de 50, el tope por página es 200 y dos páginas consecutivas no repiten ni omiten filas. |
| V6 | Edición acotada | REQ-OC-10 | Una pendiente se actualiza y recalcula; una aprobada/enviada no permite edición de negocio. |
| V7 | Máquina de estados | REQ-OC-05..07 | Cada transición permitida llega al estado esperado; cada transición no permitida falla y conserva el estado original. |
| V8 | Anulación lógica | REQ-OC-11 | No hay `DELETE`; anular mantiene la orden consultable con estado `ANULADA`. |
| V9 | Ruta y contrato | REQ-OC-12..14 | Los endpoints usan sólo `/api/tesoreria/compras/ordenCompra`; DTOs, entidades y dominio no atraviesan capas. |
| V10 | Persistencia controlada | REQ-OC-15, 16 | `ddl-auto` es `none`, el DDL fue documentado y la aplicación no crea/modifica tablas. |

## Puertas de calidad

### Arquitectura

| # | Puerta | Cómo se verifica |
|---|---|---|
| A1 | Dominio puro | Los modelos y puertos de `ordencompra/domain` no importan Spring, JPA, Jackson ni infraestructura. |
| A2 | Dependencias hacia adentro | `domain` no importa `application` ni `infrastructure`; los casos de uso dependen de puertos. |
| A3 | Dos mapeos | Existe mapper DTO↔dominio y mapper entidad↔dominio; ninguna entity llega al controller. |
| A4 | Sin inyección de campo | `rg '@Autowired' src/main` no devuelve resultados. |
| A5 | Ruta única | `rg '@RequestMapping\(\{' src/main` no devuelve resultados. |
| A6 | Sin N+1 en detalle | El adapter de detalle carga ítems e imputaciones de forma planificada y su prueba de integración lo confirma. |
| A7 | Errores acotados | El `@RestControllerAdvice` de órdenes declara `basePackages` y sólo captura excepciones propias del dominio; no intercepta `IllegalArgumentException` ni `IllegalStateException` genéricas. |
| A8 | Alta atómica | La reserva del correlativo y el alta comparten transacción; un alta fallida no deja huecos en la serie `OC-AAAA-NNNNNN`. |
| A9 | Identidad de ítems | Un cambio de estado conserva el id de cada `compra_orden_item`; no se borra y reinserta el detalle. |

### Tests y build

| # | Puerta |
|---|---|
| T1 | Tests JUnit 5 + Mockito + AssertJ para dominio, casos de uso y mappers. |
| T2 | Los tests unitarios de dominio y aplicación no necesitan MySQL, Consul ni core. |
| T3 | `./mvnw test` o `mvn test` pasa. |
| T4 | `mvn verify` pasa y JaCoCo mantiene cobertura de líneas ≥ 80 %. |
| T5 | Compilación y empaquetado Maven pasan. |
| T6 | La cobertura se mide sobre código con una sentencia por línea. Colapsar sentencias infla el ratio de JaCoCo sin agregar pruebas y deja pasar la puerta T4 con capas sin cubrir. |
| T7 | Controller, adapter, fachada y manejador de errores tienen prueba propia; ninguna clase de `infrastructure` queda en 0 % de líneas cubiertas. |
| T8 | La puerta de JaCoCo corre en `package`, sólo con tests unitarios: el 80 % no puede depender de Docker. Las reglas son BUNDLE línea ≥ 80 %, BUNDLE rama ≥ 75 % y CLASS línea ≥ 70 %. |
| T9 | Los patrones de `<excludes>` de JaCoCo terminan en `.class`; sin ese sufijo no excluyen nada. |
| T10 | `mvnw` está versionado: las puertas se corren desde un clon limpio sin instalar Maven. |
| T11 | `OrdenCompraPersistenciaIT` corre contra MySQL real con el DDL de [cambio-base-datos.md](cambio-base-datos.md) y se saltea solo si no hay Docker. |

### Documentación y operación

| # | Puerta |
|---|---|
| D1 | Caso de uso de alta, consulta y transición actualizado. |
| D2 | Diagrama de secuencia y diagrama hexagonal reflejan las capas y el circuito de persistencia. |
| D3 | El documento para DBA lista DDL, propósito y casos de uso antes de solicitar cambios. |
| O1 | Con una base que contiene el DDL aprobado, el servicio arranca sin intentar modificar el esquema. Verificado por `ArranqueAplicacionIT`: levanta la aplicación entera (`@SpringBootTest`, servidor HTTP real) contra MySQL con `ddl-auto=none`, responde `/actuator/health` UP y sirve la ruta canónica. |
| O2 | El contrato OpenAPI se publica en `/v3/api-docs` e incluye la ruta de orden de compra. Verificado en `ArranqueAplicacionIT`. |

## Cierre

1. V1 a V10 pasan.
2. Las puertas A1 a A6, T1 a T5, D1 a D3 y O1 pasan.
3. Los documentos y diagramas se actualizan en el mismo cambio que el código.
4. La feature 2 se marca como terminada en el roadmap.
