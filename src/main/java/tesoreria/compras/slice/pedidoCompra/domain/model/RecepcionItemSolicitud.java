package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

/** Ítem de la entrega a registrar (cantidad y resultado por ítem de la OC). */
public record RecepcionItemSolicitud(
        Long compraFormalizacionItemId,
        Integer orden,
        BigDecimal cantidadRecibida,
        String resultado,
        String observacion) {
}
