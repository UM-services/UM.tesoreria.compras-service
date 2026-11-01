package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record FormalizacionItemResponse(
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
