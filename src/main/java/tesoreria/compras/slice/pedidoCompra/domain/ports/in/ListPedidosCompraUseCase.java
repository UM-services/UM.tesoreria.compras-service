package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

import java.util.List;

public interface ListPedidosCompraUseCase {

    List<PedidoCompra> listar(Long solicitanteId);
}
