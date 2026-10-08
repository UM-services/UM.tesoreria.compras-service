package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraResumen;

import java.util.List;

public interface EnriquecerPedidosUseCase {

    List<PedidoCompraResumen> enriquecer(List<PedidoCompra> pedidos);
}
