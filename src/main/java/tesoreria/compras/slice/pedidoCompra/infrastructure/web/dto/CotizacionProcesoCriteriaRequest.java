package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.time.LocalDateTime;

public record CotizacionProcesoCriteriaRequest(
        String estado,
        Integer ejercicioId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
