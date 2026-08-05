# AGENTS.md

## Contexto del proyecto

`compras-service` administra el circuito de compras de tesorería. La fuente de verdad
para el alcance y las decisiones es `specs/`:

- `specs/mission.md`: propósito, límites del dominio y decisiones globales.
- `specs/tech-stack.md`: stack, arquitectura, convenciones y flujo de ramas.
- `specs/roadmap.md`: orden de features, dependencias y bloqueos.
- `specs/YYYY-MM-DD-nombre/`: requisitos, plan y validación de cada feature.

Antes de implementar un feature, leer sus tres documentos y actualizar su documentación
en el mismo cambio que el código.

## Reglas de implementación

- Mantener arquitectura hexagonal: `infrastructure → application → domain`.
- El dominio no importa Spring, JPA, Jackson ni otros frameworks.
- Un caso de uso por operación; los controllers sólo manejan DTOs y la fachada compone
  casos de uso.
- Consumir los datos existentes de `core-service` por API REST. No leer ni escribir sus
  tablas directamente.
- Usar rutas canónicas bajo `/api/tesoreria/compras/...`.
- No permitir que Hibernate modifique el esquema (`ddl-auto: none`). Los cambios de base
  se documentan y se solicitan al DBA.
- Mantener cobertura unitaria mínima de 80% con JaCoCo y un check que falle el build.

## Git y Conductor

- Cada workspace usa una rama de trabajo y se integra sólo a `develop`.
- La promoción es `develop → staging → main`; no hacer cambios directos en `staging` ni
  en `main`.
- No hacer push ni modificar configuración remota sin una indicación explícita.
- No versionar configuración local, secretos, certificados ni estado de agentes. Ver
  `.gitignore`.

## Verificación

Al terminar un cambio, ejecutar las verificaciones definidas en el `validation.md` del
feature y, como mínimo, el test suite de Maven una vez que el proyecto exista.
