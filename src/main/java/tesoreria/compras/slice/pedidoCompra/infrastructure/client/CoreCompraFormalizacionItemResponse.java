package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;

public record CoreCompraFormalizacionItemResponse(
        Long compraFormalizacionItemId,
        Long compraCotizacionItemId,
        Integer compraPedidoItemId,
        Integer orden,
        BigDecimal cantidad,
        String unidad,
        String descripcion,
        String especificaciones,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String marca) {
}
