package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Filtros de listado de pedidos. Todos son opcionales.
 *
 * <p>{@code dependenciaIds} acota a las dependencias habilitadas de un autorizante (bandeja).</p>
 */
public record PedidoCompraFiltro(
        String estado,
        Integer solicitanteId,
        Integer dependenciaId,
        List<Integer> dependenciaIds,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta
) {
}
