package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record CotizacionProcesoItemRequest(Integer compraPedidoItemId, Integer orden, BigDecimal cantidad) {
}
