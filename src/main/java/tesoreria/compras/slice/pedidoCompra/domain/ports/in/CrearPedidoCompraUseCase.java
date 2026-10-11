package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

public interface CrearPedidoCompraUseCase {

    PedidoCompra crear(Long userId, PedidoCompra pedido);
}
