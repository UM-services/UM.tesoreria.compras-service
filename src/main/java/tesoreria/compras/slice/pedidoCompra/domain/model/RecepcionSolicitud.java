package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/** Datos de la entrega a registrar: cabecera y detalle. El número y el resultado los resuelve core. */
public record RecepcionSolicitud(
        Long compraFormalizacionId,
        LocalDateTime fecha,
        String remito,
        String patrimonio,
        String observacion,
        List<RecepcionItemSolicitud> items) {
}
