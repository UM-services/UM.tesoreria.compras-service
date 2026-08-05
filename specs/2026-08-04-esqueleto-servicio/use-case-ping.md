# Caso de uso — Ping de proveedor en core

## Actor

Desarrollador o sistema consumidor de `compras-service`.

## Precondiciones

- `compras-service` está registrado en Consul.
- `tesoreria-core-service` está disponible y registrado en Consul.
- Existe el proveedor solicitado; para la validación se usa el ID `8`.

## Flujo principal

1. El actor invoca `GET /api/tesoreria/compras/ping/{proveedorId}`.
2. El controller delega en el caso de uso de proveedor.
3. El adapter Feign resuelve `tesoreria-core-service` mediante Consul.
4. Core responde `GET /api/tesoreria/core/proveedor/{proveedorId}`.
5. Compras mapea la respuesta de infraestructura a dominio y luego a DTO HTTP.
6. El actor recibe `200 OK` con los datos del proveedor.

## Flujos alternativos

- Si core no está registrado, no responde o devuelve un 5xx, el adapter lo traduce a
  `ProveedorSourceUnavailableException` y la API responde `503 Service Unavailable`.
- Si el proveedor no existe, el adapter traduce `FeignException.NotFound` a
  `ProveedorNotFoundException` y la API responde `404 Not Found`.

Feign es un detalle de infraestructura: sus excepciones no salen del adapter.

## Resultado

El endpoint demuestra que compras puede descubrir y consumir core sin una URL hardcodeada.
