package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

public record PedidoCompraItem(
        Integer compraPedidoItemId,
        Integer compraPedidoId,
        Integer orden,
        BigDecimal cantidad,
        String unidad,
        String descripcion,
        String especificaciones,
        String referenciaWeb
) {
}
