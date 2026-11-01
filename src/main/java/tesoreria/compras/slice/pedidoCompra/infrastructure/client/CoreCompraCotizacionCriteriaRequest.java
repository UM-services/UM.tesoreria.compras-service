package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.time.LocalDateTime;

public record CoreCompraCotizacionCriteriaRequest(
        String estado,
        Integer ejercicioId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
