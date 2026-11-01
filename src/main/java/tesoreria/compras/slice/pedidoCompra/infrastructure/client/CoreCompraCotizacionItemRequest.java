package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;

public record CoreCompraCotizacionItemRequest(
        Integer compraPedidoItemId,
        Integer orden,
        BigDecimal cantidad) {
}
