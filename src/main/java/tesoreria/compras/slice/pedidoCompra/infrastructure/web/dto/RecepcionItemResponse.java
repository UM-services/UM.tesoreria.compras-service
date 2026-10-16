package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record RecepcionItemResponse(
        Long compraRecepcionItemId,
        Long compraFormalizacionItemId,
        Integer orden,
        BigDecimal cantidadRecibida,
        String resultado,
        String observacion) {
}
