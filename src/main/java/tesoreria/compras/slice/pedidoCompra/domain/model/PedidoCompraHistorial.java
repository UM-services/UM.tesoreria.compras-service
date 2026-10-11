package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.time.LocalDateTime;

/**
 * Entrada de la línea de tiempo de estados de un pedido (expuesta por core).
 */
public record PedidoCompraHistorial(
        Long compraPedidoHistorialId,
        Integer compraPedidoId,
        String estado,
        Integer usuarioId,
        String observacion,
        LocalDateTime fecha
) {
}
