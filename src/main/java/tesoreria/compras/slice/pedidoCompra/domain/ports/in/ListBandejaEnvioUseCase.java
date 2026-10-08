package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

import java.util.List;

/**
 * Pedidos de las dependencias habilitadas del usuario (bandeja del autorizante).
 */
public interface ListBandejaEnvioUseCase {

    List<PedidoCompra> listar(Long userId, String estado);
}
