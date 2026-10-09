package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import java.util.List;

/**
 * Dependencias sobre las que un usuario puede decidir el envío de pedidos.
 */
public interface AutorizanteGateway {

    List<Integer> getDependenciasAutorizadas(Long userId);
}
