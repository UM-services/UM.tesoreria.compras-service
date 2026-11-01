package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RecepcionResponse(
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
        List<RecepcionItemResponse> items,
        LocalDateTime created,
        LocalDateTime updated) {
}
