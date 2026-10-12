package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record CotizacionProcesoItemResponse(
        Long compraCotizacionItemId,
        Integer compraPedidoItemId,
        Integer orden,
        BigDecimal cantidad) {
}
