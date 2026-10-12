package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

/** Alta de la Orden de Compra. */
public record FormalizacionRequest(
        Long compraCotizacionId,
        String numeroExterno,
        String moneda,
        BigDecimal monto,
        String condicionPago,
        String plazoEntrega,
        String garantia,
        String objeto,
        String observacion) {
}
