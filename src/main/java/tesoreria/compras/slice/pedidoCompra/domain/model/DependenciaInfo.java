package tesoreria.compras.slice.pedidoCompra.domain.model;

public record DependenciaInfo(
        Integer dependenciaId,
        String nombre,
        Integer facultadId,
        String facultadNombre,
        Integer geograficaId,
        String sedeNombre
) {
}
