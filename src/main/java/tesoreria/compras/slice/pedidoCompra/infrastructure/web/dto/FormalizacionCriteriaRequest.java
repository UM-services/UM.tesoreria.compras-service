package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.time.LocalDateTime;

public record FormalizacionCriteriaRequest(
        Integer ejercicioId,
        Integer proveedorId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
