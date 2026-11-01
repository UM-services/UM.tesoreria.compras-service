package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record CotizacionProcesoProveedorItemRequest(
        Long compraCotizacionItemId,
        Boolean cotiza,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String marca,
        String observacion) {
}
