package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

public record CotizacionProcesoProveedorItem(
        Long compraCotizacionProveedorItemId,
        Long compraCotizacionItemId,
        Boolean cotiza,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String marca,
        String observacion) {
}
