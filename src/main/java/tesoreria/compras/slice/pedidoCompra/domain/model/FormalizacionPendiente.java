package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;
import java.util.List;

/** Adjudicación pendiente de emitir la Orden de Compra. */
public record FormalizacionPendiente(
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
        List<FormalizacionItem> items) {
}
