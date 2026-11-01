package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Acto de adjudicación de un proceso de cotización (etapa 7). */
public record CotizacionAdjudicacion(
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
