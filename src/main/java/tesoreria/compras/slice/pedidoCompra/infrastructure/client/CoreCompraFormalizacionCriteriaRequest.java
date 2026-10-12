package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.time.LocalDateTime;

public record CoreCompraFormalizacionCriteriaRequest(
        Integer ejercicioId,
        Integer proveedorId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
