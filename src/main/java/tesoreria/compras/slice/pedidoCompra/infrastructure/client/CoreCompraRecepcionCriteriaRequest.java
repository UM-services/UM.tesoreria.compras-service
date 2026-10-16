package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.time.LocalDateTime;

public record CoreCompraRecepcionCriteriaRequest(
        Integer ejercicioId,
        Long compraFormalizacionId,
        String resultado,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
