package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.time.LocalDateTime;
import java.util.List;

public record CoreCompraRecepcionResponse(
        Long compraRecepcionId,
        Long compraFormalizacionId,
        String numero,
        Integer ejercicioId,
        LocalDateTime fecha,
        String remito,
        String resultado,
        String patrimonio,
        String observacion,
        Integer usuarioId,
        List<CoreCompraRecepcionItemResponse> items,
        LocalDateTime created,
        LocalDateTime updated) {
}
