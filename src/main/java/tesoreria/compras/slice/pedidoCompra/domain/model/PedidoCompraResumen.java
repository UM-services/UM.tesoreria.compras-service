package tesoreria.compras.slice.pedidoCompra.domain.model;

/**
 * Pedido con los nombres de dependencia y solicitante resueltos por la fachada para la
 * presentación en listados (bandeja y consulta).
 */
public record PedidoCompraResumen(
        PedidoCompra pedido,
        String dependenciaNombre,
        String solicitanteNombre
) {
}
