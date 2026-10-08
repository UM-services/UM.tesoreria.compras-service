package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

public interface AprobarPedidoCompraUseCase {

    PedidoCompra aprobar(Integer compraPedidoId, Long autorizanteId);
}
