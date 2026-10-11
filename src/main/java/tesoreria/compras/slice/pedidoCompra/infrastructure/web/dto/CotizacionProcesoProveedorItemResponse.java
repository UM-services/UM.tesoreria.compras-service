package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record CotizacionProcesoProveedorItemResponse(
        Long compraCotizacionProveedorItemId,
        Long compraCotizacionItemId,
        Boolean cotiza,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String marca,
        String observacion) {
}
