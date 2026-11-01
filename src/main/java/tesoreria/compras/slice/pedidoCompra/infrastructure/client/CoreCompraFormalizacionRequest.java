package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;

public record CoreCompraFormalizacionRequest(
        Long compraCotizacionId,
        String numeroExterno,
        String moneda,
        BigDecimal monto,
        String condicionPago,
        String plazoEntrega,
        String garantia,
        String objeto,
        String observacion,
        Integer usuarioId) {
}
