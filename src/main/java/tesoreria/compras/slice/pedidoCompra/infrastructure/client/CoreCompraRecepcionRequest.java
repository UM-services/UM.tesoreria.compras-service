package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.time.LocalDateTime;
import java.util.List;

public record CoreCompraRecepcionRequest(
        Long compraFormalizacionId,
        LocalDateTime fecha,
        String remito,
        String patrimonio,
        String observacion,
        Integer usuarioId,
        List<CoreCompraRecepcionItemRequest> items) {
}
