# Validación — Autorizantes de envío (contexto independiente)

## Criterios de aceptación

- [ ] Un usuario con dos dependencias asignadas recibe ambas en
  `GET /api/tesoreria/core/compras/autorizanteEnvio/usuario/{usuarioId}`.
- [ ] `PUT` con `{ "dependenciaIds": [1, 2] }` deja exactamente esas dos filas y elimina las que
  no estén en el cuerpo.
- [ ] `PUT` con `{ "dependenciaIds": [] }` revoca todas las dependencias del autorizante.
- [ ] `POST`/`DELETE` por dependencia son idempotentes.
- [ ] La bandeja de compras devuelve pedidos de todas las dependencias habilitadas del
  autorizante y sigue bloqueando las de otras dependencias.
- [ ] Las escrituras responden `403` sin `compras.autorizantes.configurar` cuando
  `app.permissions.enforce=true`.
- [ ] El alias `/api/tesoreria/core/compraPedidoAutorizante` responde con la forma anterior
  durante la release de transición.
- [ ] La pantalla permite marcar varias dependencias, guardar en un solo `PUT` y descartar.
- [ ] La pantalla solo es accesible con `compras.autorizantes.configurar`.

## Verificación técnica

### Core

```bash
cd um.tesoreria.core-service
mvn -q -DskipTests compile
mvn -q -Dtest='AutorizanteEnvioTest,AutorizanteEnvioUseCasesTest,AutorizanteEnvioDtoMapperTest,JpaAutorizanteEnvioRepositoryAdapterTest' test
```

### Compras

```bash
cd um.tesoreria.compras-service
./mvnw -q -DskipTests compile
./mvnw -q -Dtest='CoreAutorizanteEnvioFeignClientAdapterTest' test
```

### Frontend

```bash
cd um.tesoreria.frontend-client
npx nx test feature-administrador --skip-nx-cache
```

## Datos de prueba

```sql
-- Dos dependencias para el mismo autorizante
INSERT INTO compra_autorizante_envio_dependencia (usuario_id, dependencia_id)
VALUES (10, 1), (10, 2);

SELECT usuario_id, dependencia_id
FROM compra_autorizante_envio_dependencia
WHERE usuario_id = 10;
```

## Riesgos

- **Despliegue**: core primero y `V422` aplicado; si compras se despliega antes, el path nuevo
  no existe y la bandeja queda vacía de forma fail-closed.
- **Permisos**: `compras.autorizantes.configurar` debe asignarse a los roles o usuarios de
  compras antes de habilitar la pantalla; hasta que se asigne, el ítem del menú y la ruta quedan
  ocultos/bloqueados para todos. La clave se siembra en el catálogo, pero la asignación es
  administración de datos.
- **Concurrencia**: el `PUT` es last-write-wins; no hay bloqueo optimista.
