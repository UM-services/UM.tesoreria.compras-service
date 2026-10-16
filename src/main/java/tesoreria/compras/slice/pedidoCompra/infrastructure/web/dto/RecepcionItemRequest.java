package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

/** Ítem recibido de la entrega (cantidad y resultado por ítem de la OC). */
public record RecepcionItemRequest(
        Long compraFormalizacionItemId,
        Integer orden,
        BigDecimal cantidadRecibida,
        String resultado,
        String observacion) {
}
