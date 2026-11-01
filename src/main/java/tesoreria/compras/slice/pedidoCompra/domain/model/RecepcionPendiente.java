package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Orden de Compra con recepción pendiente (bandeja de recepción). */
public record RecepcionPendiente(
        Long compraFormalizacionId,
        String numero,
        String numeroExterno,
        Integer ejercicioId,
        Integer proveedorId,
        LocalDateTime fecha,
        BigDecimal monto,
        String moneda,
        List<RecepcionPendienteItem> items,
        List<Recepcion> recepciones,
        Boolean completo) {
}
