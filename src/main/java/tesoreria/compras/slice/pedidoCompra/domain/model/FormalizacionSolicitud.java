package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

/** Datos editables del alta de la Orden de Compra. */
public record FormalizacionSolicitud(
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
