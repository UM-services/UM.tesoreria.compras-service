# AGENTS.md

## Project context

`compras-service` manages the treasury purchasing workflow. The source of truth
for scope and decisions is `specs/`:

- `specs/tech-stack.md`: stack, architecture, conventions and branch flow.
- `specs/roadmap.md`: feature order, dependencies and blockers.
- `specs/YYYY-MM-DD-name/`: requirements, plan and validation for each feature.

Before implementing a feature, read its three documents and update its documentation
in the same change as the code.

## Implementation rules

- Keep hexagonal architecture: `infrastructure → application → domain`.
- The domain does not import Spring, JPA, Jackson or other frameworks.
- One use case per operation; controllers only handle DTOs and the facade composes
  use cases.
- Consume existing `core-service` data over REST. Do not read or write its tables
  directly.
- Use canonical routes under `/api/tesoreria/compras/...`.
- Do not let Hibernate modify the schema (`ddl-auto: none`). Database changes are
  documented and requested from the DBA.
- Keep the JaCoCo gates that fail the build: 80% line and 75% branch over the bundle,
  and a 70% line floor per class. Coverage is measured over code with one statement
  per line; collapsing statements inflates the ratio without adding tests.
- Annotate every change with impact in `CHANGELOG.md`, in the same commit as the code.
  Keep a Changelog format: unpublished work goes under `## [Unreleased]`, and on release
  it moves to its version number together with `VERSION` and the `pom.xml` `<version>`.
  The changelog belongs to this repository: findings about `core-service` or other
  services go in `specs/`.

## Git and Conductor

- Each workspace uses a working branch and integrates only into `develop`.
- Promotion is `develop → staging → main`; do not make direct changes on `staging` or
  `main`.
- Do not push or modify remote configuration without an explicit instruction.
- Do not version local configuration, secrets, certificates or agent state. See
  `.gitignore`.

## Verification

When finishing a change, run the verifications defined in the feature's `validation.md`
and, at minimum, the Maven test suite once the project exists.

## Permissions (feature gating)

The permission system is **centralized** and lives in `tesoreria-core-service` (catalog
`permiso`, roles, role×permission matrix, overrides and the effective bundle
`GET /api/tesoreria/core/permisoEfectivo/usuario/{userId}`). Keys follow the
`modulo.accion` convention (e.g. `compras.orden_aprobar`).

- If you add a new feature that requires a permission: register the key in the catalog
  (from the administrador module) and wire it in the **new** code. Server-side
  enforcement (`@RequierePermiso`) lives in core; this service has **none**.
- **Golden rule:** never add or change security on existing endpoints or legacy code.
  The legacy system sends no token or permissions: any enforcement there breaks it.
  When in doubt, stop and ask.

## Pedido de compra (fachada)

- Slice `slice/pedidoCompra`: fachada del circuito "iniciar pedido" (contexto, alta/edición,
  envío y consulta). Consume `core-service` por Feign; **no persiste**.
- Endpoints canónicos `/api/tesoreria/compras/pedido...`; la identidad llega por el header
  `X-User-Id` (transitorio hasta el JWT de M2).
- Permiso **`compras.iniciar_pedido`**: la fachada lo verifica consultando
  `GET /api/tesoreria/core/permisoEfectivo/usuario/{userId}` y responde `403` si falta.
- El **gateway** inyecta `X-API-Key` en `/api/tesoreria/compras/**` (el navegador no la tiene);
  el filtro está acotado a esa ruta nueva, así que no afecta al legacy.
