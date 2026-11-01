package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

public record CotizacionProcesoItem(
        Long compraCotizacionItemId,
        Integer compraPedidoItemId,
        Integer orden,
        BigDecimal cantidad) {
}
