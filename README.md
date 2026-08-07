# UM Tesoreria Compras Service

Servicio responsable del circuito de compras de tesorería. La planificación y las reglas
del proyecto viven en [`specs/`](specs/).

## Requisitos locales

- Docker y Docker Compose.
- La red externa `tesoreria-shared`.
- El stack compartido con Consul y `tesoreria-core-service` levantado.

El proyecto compila con Java 25 dentro de Docker; no requiere Maven ni Java instalados en
la máquina anfitriona.

## Ejecutar

```bash
docker compose build tesoreria-compras-service
docker compose up -d tesoreria-compras-service
```

El servicio queda disponible en `http://localhost:8203`.

## Verificar el esqueleto

```bash
curl http://localhost:8203/actuator/health
curl http://localhost:8203/api/tesoreria/compras/ping/8
```

El segundo comando consulta `core-service` mediante Feign y Consul. El proveedor 8 debe
responder con razón social `Roberto Mario Cerutti`.

El acceso por gateway se implementa y valida como una integración separada cuando un
cliente externo lo necesite. La validación de este servicio se realiza por su puerto
directo.

## Build y pruebas

```bash
./mvnw verify
```

Corre las pruebas unitarias, la puerta de JaCoCo y las pruebas de integración. El wrapper
baja Maven solo; hace falta un JDK 25.

La puerta de cobertura se evalúa en `package`, sólo con las pruebas unitarias: el 80 % no
depende de Docker. Las reglas son línea ≥ 80 %, rama ≥ 75 % y un piso por clase de línea
≥ 70 %, para que el promedio no tape una capa entera sin cubrir.

### Pruebas de integración

`OrdenCompraPersistenciaIT` levanta un MySQL real con Testcontainers y le aplica el DDL de
[cambio-base-datos.md](specs/2026-08-05-orden-de-compra/cambio-base-datos.md). Es lo único
que verifica la numeración concurrente, que un cambio de estado conserve los ids de los
ítems y que las entidades JPA calcen con el esquema que se le pide al DBA.

Necesita Docker. **Con Colima, Testcontainers no lo detecta solo** y hay que exportar:

```bash
export DOCKER_HOST="unix://$HOME/.colima/default/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
```

Sin Docker las pruebas se saltean y el build sigue en verde, así que avisan por stderr.
Para que se caigan en vez de saltearse, correr con `REQUIRE_DOCKER=true` (es lo que hace
CI): un build verde tiene que significar que estas pruebas corrieron.
