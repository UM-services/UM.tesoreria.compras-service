package tesoreria.compras.slice.pedidoCompra.domain.model;

/**
 * Pedido con los datos de dependencia y solicitante resueltos por la fachada para la
 * presentación en listados y detalle (bandeja, consulta y respuestas unitarias).
 */
public record PedidoCompraResumen(
        PedidoCompra pedido,
        String dependenciaNombre,
        String solicitanteNombre,
        String solicitanteLogin
) {
}
