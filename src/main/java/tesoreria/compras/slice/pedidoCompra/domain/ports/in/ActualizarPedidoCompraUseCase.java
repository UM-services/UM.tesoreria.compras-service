package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

public interface ActualizarPedidoCompraUseCase {

    PedidoCompra actualizar(Integer compraPedidoId, PedidoCompra pedido);
}
