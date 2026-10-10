package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

/**
 * Autorización del inicio del proceso de pedido de presupuesto (autoridad por monto).
 */
public interface AutorizarPresupuestoPedidoCompraUseCase {

    PedidoCompra autorizar(Integer compraPedidoId, Long usuarioId);
}
