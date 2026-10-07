package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

public interface GetPedidoCompraUseCase {

    PedidoCompra getById(Integer compraPedidoId);
}
