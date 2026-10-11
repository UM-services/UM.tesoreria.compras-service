package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CoreCompraCotizacionRespuestaRequest(
        LocalDateTime fechaRespuesta,
        String moneda,
        BigDecimal total,
        String plazoEntrega,
        String condicionPago,
        String garantia,
        String validezOferta,
        List<CoreCompraCotizacionProveedorItemRequest> items) {
}
