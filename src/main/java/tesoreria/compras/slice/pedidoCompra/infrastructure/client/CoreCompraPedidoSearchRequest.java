package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cuerpo de la búsqueda de pedidos en core ({@code POST /compraPedido/search}).
 */
public record CoreCompraPedidoSearchRequest(
        String estado,
        Integer solicitanteId,
        Integer dependenciaId,
        List<Integer> dependenciaIds,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta
) {
}
