package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

/** Adjudicación pendiente de formalizar (bandeja de la Orden de Compra). */
public record FormalizacionPendienteResponse(
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
        List<FormalizacionItemResponse> items) {
}
