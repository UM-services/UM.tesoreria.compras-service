package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.time.LocalDateTime;

/** Filtros opcionales de la consulta de pedidos, en el cuerpo del POST. */
public record ConsultaPedidoRequest(
        String estado,
        Integer solicitanteId,
        Integer dependenciaId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta
) {
}
