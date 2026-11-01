package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;

public record CoreAdjudicarCompraCotizacionRequest(
        String decision,
        Long compraCotizacionProveedorId,
        Integer proveedorAdjudicadoId,
        BigDecimal monto,
        String fundamento,
        Long autoridadUsuarioId,
        BigDecimal limiteAplicado) {
}
