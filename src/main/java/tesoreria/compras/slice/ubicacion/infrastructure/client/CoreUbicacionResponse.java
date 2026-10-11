package tesoreria.compras.slice.ubicacion.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreUbicacionResponse(
        Integer ubicacionId,
        String nombre,
        Integer dependenciaId,
        Integer geograficaId
) {
}
