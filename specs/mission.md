# Misión — UM.tesoreria.compras-service

Documento global: **por qué existe este servicio y bajo qué reglas se construye.** No
cambia por feature. Lo que sí cambia por feature vive en `specs/YYYY-MM-DD-nombre/`.

---

## Alcance — leer esto primero

**Este servicio es autocontenido.** Lo que construye tiene que poder desarrollarse,
testearse y validarse **con su propio código**, sin depender de que otro equipo toque su
repo ni de que otro servicio exponga algo.

| Es de este servicio | No lo es |
|---|---|
| La **orden de compra**: modelo, estados, persistencia, consultas | Las facturas — viven en `ProveedorMovimiento`, en core |
| La **cadena de aprobación** por monto, con sus umbrales | Las órdenes de pago — viven en `ProveedorPago`, en core |
| Sus **propias tablas** | Los maestros de proveedores y artículos — de core |
| Sus endpoints bajo `/api/tesoreria/compras/...` | El ruteo del gateway — otro repo |
| | El envío de mails y PDFs — `sender-service` y `report-service` |

**La regla:** si no lo puedo construir y validar con mi propio código, no es un feature
mío. Es integración, y se hace cuando haya algo concreto que integrar.

**Corolario práctico:** un criterio de aceptación que depende del repo de otro no bloquea
un feature. La orden guarda `proveedorId` como identificador y no valida contra core que
exista: es una referencia, no una relación.

## El problema

El circuito de gastos de la universidad —**COMPRAS → PROVEEDORES → PAGOS**— está modelado
a medias. `core-service` ya administra proveedores, artículos, facturas y contabilidad.

Lo que **no existe en ningún lado** es la **orden de compra**: el documento que autoriza un
gasto antes de comprometer el dinero. Verificado contra el código de core: ni modelo, ni
tabla, ni endpoint.

Sin ella no hay cadena de aprobación exigible, ni vínculo entre lo que se acordó comprar y
lo que se facturó. Hoy eso vive en mails, planillas y Drive.

**Ésa es la brecha que llena este servicio.**

## Cómo se ve el éxito

Que una orden de compra se pueda generar, aprobar según su monto, seguir y anular, con la
historia entera consultable.

El documento fuente describe un circuito más largo —factura, imputación, pago,
notificación al proveedor—, pero esas piezas viven en otros servicios. Tienen sentido
**después**, cuando la orden exista y esté en uso. Ver el roadmap.

## Roles

Los cargos del circuito se modelan como **roles**. No se representan mediante flags
booleanos por acción en el usuario.

| Rol | Qué hace |
|---|---|
| **Director de Compras** | Genera la orden; la envía al proveedor una vez aprobada |
| **Director de Administración (DA)** | Aprueba por monto; autoriza pagos; aprueba anulaciones; recibe alarmas de desvío |
| **Secretario Administrativo** / **Director de Gestión** | Segundo nivel de aprobación |
| **Rector** | Nivel máximo; aprueba también los umbrales |
| **Director de Contabilidad** | Designa quién carga facturas; genera asientos |
| **Sector Proveedores** | Carga facturas contra órdenes (control por oposición) |
| **Tesorero** | Ejecuta el pago bancario y lo registra |

---

## Principios de arquitectura — no negociables

Este servicio se evalúa por su arquitectura, no sólo por si funciona. `core-service` es la
implementación de referencia. **Ante la duda, copiar cómo lo hace core.** El detalle con
código verificado está en [tech-stack.md](tech-stack.md).

1. **Hexagonal en serio.** Las dependencias apuntan sólo hacia adentro:
   `infrastructure → application → domain`. El modelo de dominio **no importa frameworks**:
   ni Spring, ni JPA, ni Jackson. Nunca.
2. **Un caso de uso por operación.** Una interfaz, un método, una implementación. La
   fachada `@Service` compone; no contiene lógica.
3. **Dos fronteras de mapeo, nunca salteadas.** Una entity JPA jamás llega a un
   controller; un DTO jamás llega al dominio.
4. **Ruta única canónica** `/api/tesoreria/compras/...`. La ruta dual que se ve en core es
   un artefacto de su migración, no un patrón a copiar.
5. **Las fronteras entre servicios son reales.** Este servicio es dueño del dominio de
   compras y lo persiste. Lo que ya existe en core se **lee por API REST**, nunca se
   duplica ni se escribe. Dos servicios no escriben la misma tabla.
6. **Cobertura ≥ 80%, siempre.** Tests unitarios con JaCoCo, y el check configurado para
   **fallar el build** si baja. No es una meta para el final: se sostiene commit a commit.
   La arquitectura hexagonal lo hace barato — si cuesta llegar, es señal de que hay lógica
   en el lugar equivocado.
7. **Documentado a medida que se construye.** Ver más abajo.

## Restricciones del dominio

- **Una orden de compra no genera devengamiento.** Sólo la factura. No hay módulo de
  presupuesto, así que la OC no afecta partidas.
- **No hay restricción cronológica** entre fecha de OC y fecha de factura. Contraintuitivo,
  pero es así: un validador "la factura no puede ser anterior a la OC" sería **incorrecto**.
- **Control por oposición**: quien carga la factura no es quien generó la orden.
- **El esquema se administra fuera de la aplicación** (`ddl-auto: none`). Nunca lo toca
  Hibernate.

---

## Decisiones vigentes

| # | Decisión | Motivo |
|---|---|---|
| D1 | El dominio de orden de compra se construye acá, no en core | Decisión del equipo |
| D2 | Este servicio **es dueño de su persistencia** | Consecuencia de D1 |
| D3 | Lo que ya existe en core se **lee por API**, nunca se duplica ni se escribe | Fronteras de servicio |
| D4 | **Ruta única canónica** `/api/tesoreria/compras/...` | La dual es artefacto de migración de core |
| D5 | Arquitectura **hexagonal estricta**, copiando core | Estándar del equipo |
| D6 | Los mails salen por `sender-service`; los PDF, revisar `report-service` antes de construir | No reinventar |
| D7 | Un `docker-compose.yml` **por repositorio**, red compartida `tesoreria-shared` | Cada servicio debe poder levantarse desde su propio repositorio, sin rutas relativas a otros repositorios |
| D8 | La conectividad a la base se configura localmente y no se versiona | Cada entorno puede requerir una configuración de red distinta |
| D9 | Los cambios de base se consensúan, documentan y solicitan al DBA para su aplicación | Indicación del equipo |
| D10 | El gateway es obligatorio para probar el recorrido de un cliente externo; no para desarrollar ni testear este servicio | Indicación del equipo |
| D11 | **`umhub-service` es el molde** estructural (chico, hexagonal) | Sugerencia del equipo |
| D12 | **La documentación es entregable**: casos de uso y diagramas de secuencia | Indicación del equipo: *"indispensable"* |
| D13 | Los cargos se modelan como **roles**, no como flags por acción | Decisión del equipo |
| D14 | El detalle de una orden de compra se devuelve completo en **una sola llamada** | Evitar llamadas adicionales y el patrón N+1 en clientes |

## Documentación como entregable (D12)

Pedido explícito: *"para todas las tareas deberíamos elaborar documentación
(indispensable): casos de uso, diagramas de secuencia, lo que nos venga mejor para
entender lo que estamos haciendo"*.

Aplica a **todo feature**, y va en su carpeta `specs/YYYY-MM-DD-nombre/`:

| ID | Requerimiento |
|---|---|
| REQ-DOC-01 | Casos de uso: actor, precondición, flujo principal, flujos alternativos |
| REQ-DOC-02 | Diagrama de secuencia Mermaid `.mmd` de los flujos entre servicios |
| REQ-DOC-03 | Diagrama hexagonal `docs/hexagonal-<agregado>.mmd` por agregado |
| REQ-DOC-04 | Los diagramas se actualizan **en el mismo commit** que el código. Un diagrama desactualizado es peor que ninguno |
| REQ-DOC-05 | Todo cambio de base se documenta **antes** de pedírselo al DBA: qué campo, de qué tipo, por qué, y qué caso de uso lo necesita |

---

## Fuera de alcance

**Por la regla de alcance** — vive en otro servicio, se integra más adelante:

- Conciliación de facturas contra órdenes (`ProveedorMovimiento`, en core)
- Imputación y centros de costo (maestros de core)
- Pagos y tesorería (`ProveedorPago`, en core, y además Kotlin legacy sin REST)
- Envío de PDFs y mails (`report-service`, `sender-service`)
- El ruteo del gateway (repo de otro equipo, con permiso de lectura nomás)

**Por el documento fuente**, que lo señala así:

- **Retenciones impositivas** — *"NO TIENE QUE VER CON COMPRAS – ES PARA IR VIENDO"*.
- **Adjuntar comprobantes** — *"NO ES PRIORITARIO"*. Ojo: PALADINI 1.d insinúa algo mucho
  mayor (llevar todo el hilo de pedidos y aprobaciones al sistema). Proyecto aparte.
- **Control de stock** en artículos — se asume **no** hasta que digan lo contrario.
- **Órdenes de trabajo** — obras con presupuesto total consumido por *certificados* y pagos
  a cuenta. Es un **modelo de cancelación distinto**, no un flag de tipo. Postergado.
- **Reemplazar la planilla de Drive** — la alimentamos, no la eliminamos.

## Glosario

| Término | Qué significa |
|---|---|
| **Imputación** | A qué parte de la estructura contable se carga un gasto |
| **Devengamiento** | Reconocer contablemente el gasto. Lo genera la factura, **no** la orden |
| **Partidas** | Rubros del presupuesto. Acá no aplica: no hay módulo de presupuesto |
| **Aval de OC** | Que un gasto tenga una orden de compra que lo respalde |
| **Control por oposición** | Que quien carga la factura no sea quien generó la orden |
| **Legajo** | El conjunto completo de documentación de una operación |
| **Seña / anticipo (ANT)** | Pago hecho antes de tener la factura |
| **Remito** | Documento que acompaña la entrega de mercadería |
| **Certificado** | En obras, avance aprobado que habilita un pago a cuenta |
| **Fondos fijos** | Caja chica para gastos menores |
| **Base de asignación** | Regla que divide una factura entre destinos (por m², alumnos, etc.) |
| **Chequera** | Contexto de core sobre cuotas de alumnos. No es parte de compras |
| **Hexagonal** | Arquitectura donde el dominio no conoce frameworks; todo entra y sale por puertos |

## Documento fuente

`Circuito orden de compra y carga de facturas.docx.pdf` — 5 páginas, dos partes:
el análisis propio de la universidad (§1–§5) y un recorrido de referencia del circuito
deseado (PALADINI 1–9). Las citas textuales se conservan en cada `requirements.md` de
feature, para que las decisiones sean trazables.
