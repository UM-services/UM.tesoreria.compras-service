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

El repositorio ya no lleva un `docker-compose.yml` propio: el servicio se define en el
compose compartido del equipo. Para una imagen local alcanza con el `Dockerfile`:

```bash
docker build -t tesoreria-compras-service .
docker run -d --name tesoreria-compras-service \
  --network tesoreria-shared \
  -p 8096:8096 \
  tesoreria-compras-service
```

El servicio queda disponible en `http://localhost:8096`.

## Verificar el esqueleto

```bash
curl http://localhost:8096/actuator/health
curl -H "X-API-Key: $APP_API_KEY" http://localhost:8096/api/tesoreria/compras/ping/8
```

El segundo comando consulta `core-service` mediante Feign y Consul y devuelve el proveedor
8 con los datos reales de core. Todos los endpoints, salvo
`/actuator`, `/swagger-ui` y `/v3/api-docs`, exigen el header `X-API-Key` con la clave de
`app.api-key` (`APP_API_KEY`).

El acceso por gateway se implementa y valida como una integración separada cuando un
cliente externo lo necesite. La validación de este servicio se realiza por su puerto
directo.

## Build y pruebas

```bash
./mvnw verify
```

Corre las pruebas unitarias y la puerta de JaCoCo. El wrapper baja Maven solo; hace falta
un JDK 25.

La puerta de cobertura se evalúa en `package`, sólo con las pruebas unitarias: el 80 % no
depende de Docker. Las reglas son línea ≥ 80 %, rama ≥ 75 % y un piso por clase de línea
≥ 70 %, para que el promedio no tape una capa entera sin cubrir.
