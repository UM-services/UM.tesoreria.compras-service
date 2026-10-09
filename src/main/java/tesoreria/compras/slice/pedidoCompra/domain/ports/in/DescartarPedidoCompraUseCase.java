package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

public interface DescartarPedidoCompraUseCase {

    PedidoCompra descartar(Integer compraPedidoId, Long usuarioId, String motivo);
}
