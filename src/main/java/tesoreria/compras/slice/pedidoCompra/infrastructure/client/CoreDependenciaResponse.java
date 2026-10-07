package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreDependenciaResponse(
        Integer dependenciaId,
        String nombre,
        Integer facultadId,
        Integer geograficaId,
        CoreFacultadResponse facultad,
        CoreGeograficaResponse geografica
) {
}
