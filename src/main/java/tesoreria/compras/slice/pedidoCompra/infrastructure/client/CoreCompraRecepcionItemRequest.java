package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;

public record CoreCompraRecepcionItemRequest(
        Long compraFormalizacionItemId,
        Integer orden,
        BigDecimal cantidadRecibida,
        String resultado,
        String observacion) {
}
