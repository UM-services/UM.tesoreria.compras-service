package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

/**
 * Contexto del formulario "iniciar pedido": todo lo que el frontend necesita en una sola llamada.
 */
public record ContextoInicioPedidoResponse(
        SolicitanteResponse solicitante,
        DependenciaContextoResponse dependencia
) {
    public record SolicitanteResponse(Long userId, String nombre, String login) {
    }

    public record DependenciaContextoResponse(
            Integer dependenciaId,
            String nombre,
            Integer facultadId,
            String facultadNombre,
            Integer geograficaId,
            String sedeNombre
    ) {
    }
}
