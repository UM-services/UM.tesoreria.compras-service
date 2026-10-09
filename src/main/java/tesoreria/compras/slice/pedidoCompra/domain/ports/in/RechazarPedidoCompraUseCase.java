package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

public interface RechazarPedidoCompraUseCase {

    PedidoCompra rechazar(Integer compraPedidoId, Long autorizanteId, String motivo);
}
