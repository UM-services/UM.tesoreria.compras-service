package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

/**
 * Rechazo de la autorización de presupuesto (autoridad por monto).
 */
public interface RechazarPresupuestoPedidoCompraUseCase {

    PedidoCompra rechazar(Integer compraPedidoId, Long usuarioId, String motivo);
}
