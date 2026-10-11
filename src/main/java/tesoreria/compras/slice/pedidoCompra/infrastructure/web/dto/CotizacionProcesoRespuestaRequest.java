package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CotizacionProcesoRespuestaRequest(
        LocalDateTime fechaRespuesta,
        String moneda,
        BigDecimal total,
        String plazoEntrega,
        String condicionPago,
        String garantia,
        String validezOferta,
        List<CotizacionProcesoProveedorItemRequest> items) {
}
