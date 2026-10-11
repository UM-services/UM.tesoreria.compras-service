package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.ContextoInicioPedido;

public interface GetContextoInicioPedidoUseCase {

    ContextoInicioPedido getContexto(Long userId);
}
