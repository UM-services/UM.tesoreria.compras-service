package tesoreria.compras.ordencompra.domain.model;

import java.math.BigDecimal;

public record OrdenCompraItem(Integer articuloId, String descripcion, BigDecimal cantidad,
                               BigDecimal precioUnitario, Long imputacionId) {
    public OrdenCompraItem {
        if (articuloId == null || imputacionId == null || cantidad == null || precioUnitario == null
                || cantidad.signum() <= 0 || precioUnitario.signum() < 0) {
            throw new IllegalArgumentException("El ítem debe tener referencias e importes válidos");
        }
    }

    public BigDecimal subtotal() {
        return cantidad.multiply(precioUnitario);
    }
}
