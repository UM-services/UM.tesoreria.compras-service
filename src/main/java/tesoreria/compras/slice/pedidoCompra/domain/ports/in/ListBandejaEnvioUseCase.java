package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.BandejaEnvio;

/**
 * Pedidos de las dependencias habilitadas del usuario (bandeja del autorizante).
 */
public interface ListBandejaEnvioUseCase {

    BandejaEnvio listar(Long userId, String estado);
}
