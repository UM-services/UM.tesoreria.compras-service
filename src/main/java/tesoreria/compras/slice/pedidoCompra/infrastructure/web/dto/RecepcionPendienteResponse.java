package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Orden de Compra con recepción pendiente (bandeja de recepción). */
public record RecepcionPendienteResponse(
        Long compraFormalizacionId,
        String numero,
        String numeroExterno,
        Integer ejercicioId,
        Integer proveedorId,
        LocalDateTime fecha,
        BigDecimal monto,
        String moneda,
        List<RecepcionPendienteItemResponse> items,
        List<RecepcionResponse> recepciones,
        Boolean completo) {
}
