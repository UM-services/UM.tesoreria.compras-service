package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CotizacionAdjudicacionResponse(
        Long compraAdjudicacionId,
        Long compraCotizacionId,
        Integer ejercicioId,
        Long compraCotizacionProveedorId,
        Integer proveedorAdjudicadoId,
        BigDecimal monto,
        String decision,
        String fundamento,
        Long autoridadUsuarioId,
        BigDecimal limiteAplicado,
        LocalDateTime fecha) {
}
