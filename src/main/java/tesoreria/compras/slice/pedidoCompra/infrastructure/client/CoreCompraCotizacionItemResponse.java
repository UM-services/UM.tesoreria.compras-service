package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;

public record CoreCompraCotizacionItemResponse(
        Long compraCotizacionItemId,
        Integer compraPedidoItemId,
        Integer orden,
        BigDecimal cantidad) {
}
