package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

/** Ítem de la Orden de Compra (snapshot del proceso y la oferta adjudicada). */
public record FormalizacionItem(
        Long compraFormalizacionItemId,
        Long compraCotizacionItemId,
        Integer compraPedidoItemId,
        Integer orden,
        BigDecimal cantidad,
        String unidad,
        String descripcion,
        String especificaciones,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String marca) {
}
