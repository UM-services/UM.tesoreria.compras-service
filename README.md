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
