# Roadmap — UM.tesoreria.compras-service

Lo que este servicio construye, en orden.

- El **cómo** → [tech-stack.md](tech-stack.md)

**Sólo entra al roadmap lo que este servicio puede construir y validar solo.** Lo que
necesita código de otro repo o datos que viven en otro servicio no es un feature nuestro:
es integración, y está más abajo, sin bloquear nada.

Cada feature abre su carpeta `specs/YYYY-MM-DD-nombre/` cuando se empieza a trabajar en
él, no antes.

---

## Estado

| # | Feature | Estado |
|---|---|---|
| 0 | Entorno local | ✅ terminado |
| 1 | [Esqueleto del servicio](2026-08-04-esqueleto-servicio/) | ✅ terminado |
| 2 | [Fachada gateada de Gastos y Proveedores](2026-10-09-fachada-gastos-proveedores/) | 🚧 en curso |

El plan de features se redefine con el nuevo alcance. Todo lo demás del circuito
—facturas, imputación, pagos, notificaciones— vive en core o en otros servicios: ver
*Integración* más abajo.

---

## 0 — Entorno local ✅

El stack de desarrollo (Consul, Zookeeper, Kafka y core) levanta y responde contra la base
de desarrollo.

Las notas de setup específicas de una máquina no van acá: viven en la guía local de cada
desarrollador.

## 1 — Esqueleto del servicio ✅

`compras-service` levanta en 8096, se registra en Consul como `tesoreria-compras-service`,
y consume un proveedor real de core por Feign. Molde: `umhub-service`.

→ [`2026-08-04-esqueleto-servicio/`](2026-08-04-esqueleto-servicio/)

---

## Integración — más adelante

Nada de esto bloquea un feature. Se hace cuando haya algo concreto que integrar, y se
valida en ese momento.

### Estado de core-service

Relevado al construir el esqueleto (feature 1), útil para dimensionar cualquier integración:

- Core está al **70% hexagonal** (1018 de 1460 archivos Java). El 30% restante es legacy,
  más 70 archivos Kotlin.
- Core **no exige `X-API-Key`** para proveedores. El `ApiKeyFilter` es propio de umhub;
  core no tiene ese filtro. Su contrato usa camelCase y `habilitado` numérico.

### Ruta en el gateway

Resuelta: la ruta vive en el `bootstrap.yml` de `um.tesoreria.gateway-service` (repo
aparte), delante de los demás servicios de tesorería.

```yaml
- id: tesoreria-compras-service
  uri: lb://tesoreria-compras-service
  predicates:
    - Path=/api/tesoreria/compras/**
```

### Maestro de artículos

Resuelta: el slice `articulo` consume el maestro de `core-service` por Feign
(`GET /api/tesoreria/core/articulo/{id}`, `POST /api/tesoreria/core/articulo/search`),
con el mismo patrón que el proveedor: puerto de salida propio y adapter que traduce los
errores de Feign. Queda disponible para resolver nombres de artículos.

### El resto del circuito

El documento fuente describe el circuito completo de la universidad, que va bastante más
allá de este servicio. Estas piezas **viven en core o en otros servicios**, y sólo tienen
sentido cuando alguna se vuelva trabajo real:

| Pieza | Dónde vive hoy | Qué haría falta |
|---|---|---|
| Conciliación de facturas | `ProveedorMovimiento`, en core | Vincular las facturas con la operación; tolerancia y desvíos |
| Imputación y centros de costo | Maestros en core | Tres niveles, bases de asignación |
| Pagos y tesorería | `ProveedorPago`, en core — **Kotlin legacy sin controller REST** | Definir primero quién es dueño de la orden de pago |
| Envío del PDF al proveedor | `report-service` (8281), `sender-service` (8188) | Revisar qué hace report antes de construir nada |
| Notificaciones al proveedor | `sender-service` | Dos mails: aprobación y pago ejecutado |

No están numeradas a propósito. Cuando alguna se vuelva trabajo real, se le abre carpeta y
entra al roadmap.

---

## Falta definir

Lo que se puede mirar sin preguntarle a nadie:

- Qué API consume el `tesoreria-compras-client` que ya existe (puerto 4201). O ya le habla
  a core, o espera un contrato, o es un stub.

## Ya resuelto

| # | Resolución |
|---|---|
| B3 | La ruta del gateway es integración posterior, no bloqueo |
| B10 | Puerto 8096 |

---

## Cómo abrir un feature nuevo

1. Crear `specs/YYYY-MM-DD-nombre/` con la fecha en que se empieza.
2. Adentro: `requirements.md` (qué hace, con sus `REQ-*`), `plan.md` (tareas) y
   `validation.md` (criterios).
3. Actualizar la tabla de estado.
4. Lo global no se repite: se referencia.

**Antes de agregar un feature, la pregunta es una sola:** ¿lo puedo construir y validar
con mi propio código? Si la respuesta es no, es integración.
