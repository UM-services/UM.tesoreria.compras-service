package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

/** Ítem recibido de una entrega: cantidad y resultado por ítem de la OC. */
public record RecepcionItem(
        Long compraRecepcionItemId,
        Long compraFormalizacionItemId,
        Integer orden,
        BigDecimal cantidadRecibida,
        String resultado,
        String observacion) {
}
