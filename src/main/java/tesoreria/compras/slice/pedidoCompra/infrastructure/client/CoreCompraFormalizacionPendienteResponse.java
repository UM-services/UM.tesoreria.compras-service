package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;
import java.util.List;

public record CoreCompraFormalizacionPendienteResponse(
        Long compraCotizacionId,
        String numero,
        Integer ejercicioId,
        Long compraAdjudicacionId,
        Long compraCotizacionProveedorId,
        Integer proveedorId,
        BigDecimal monto,
        String moneda,
        String condicionPago,
        String plazoEntrega,
        String garantia,
        List<CoreCompraFormalizacionItemResponse> items) {
}
