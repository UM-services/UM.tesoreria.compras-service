package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

/** Ítem contratado de la OC con su saldo derivado. */
public record RecepcionPendienteItem(
        Long compraFormalizacionItemId,
        Integer compraPedidoItemId,
        Integer orden,
        BigDecimal cantidad,
        BigDecimal recibido,
        BigDecimal saldo,
        String unidad,
        String descripcion,
        String especificaciones,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String marca,
        Boolean completo) {
}
