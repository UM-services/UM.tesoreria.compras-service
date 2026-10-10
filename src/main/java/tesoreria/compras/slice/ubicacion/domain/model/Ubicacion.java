package tesoreria.compras.slice.ubicacion.domain.model;

public record Ubicacion(
        Integer ubicacionId,
        String nombre,
        Integer dependenciaId,
        Integer geograficaId
) {
}
