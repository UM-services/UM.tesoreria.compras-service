package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraHistorial;

import java.util.List;

public interface HistorialGateway {

    List<PedidoCompraHistorial> listar(Integer compraPedidoId);
}
