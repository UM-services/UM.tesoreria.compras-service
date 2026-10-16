package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Recepción y conformidad (etapa 9) resuelta en {@code core}: una entrega contra una OC. */
public record Recepcion(
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
        List<RecepcionItem> items,
        LocalDateTime created,
        LocalDateTime updated) {
}
