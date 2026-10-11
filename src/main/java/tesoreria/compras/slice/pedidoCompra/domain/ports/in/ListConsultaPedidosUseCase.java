package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;

import java.util.List;

/**
 * Consulta global del estado de todos los pedidos, con filtros.
 */
public interface ListConsultaPedidosUseCase {

    List<PedidoCompra> listar(PedidoCompraFiltro filtro);
}
