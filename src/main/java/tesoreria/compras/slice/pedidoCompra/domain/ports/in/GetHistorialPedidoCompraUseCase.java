package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraHistorial;

import java.util.List;

public interface GetHistorialPedidoCompraUseCase {

    List<PedidoCompraHistorial> listar(Integer compraPedidoId);
}
