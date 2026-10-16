package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Alta de una entrega contra una Orden de Compra. */
public record RecepcionRequest(
        Long compraFormalizacionId,
        LocalDateTime fecha,
        String remito,
        String patrimonio,
        String observacion,
        List<RecepcionItemRequest> items) {
}
