package tesoreria.compras.slice.ubicacion.infrastructure.web.dto;

public record UbicacionResponse(
        Integer ubicacionId,
        String nombre,
        Integer dependenciaId,
        Integer geograficaId
) {
}
