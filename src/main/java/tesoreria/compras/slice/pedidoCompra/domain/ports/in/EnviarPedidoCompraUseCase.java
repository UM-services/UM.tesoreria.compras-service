package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

public interface EnviarPedidoCompraUseCase {

    PedidoCompra enviar(Long userId, Integer compraPedidoId);
}
