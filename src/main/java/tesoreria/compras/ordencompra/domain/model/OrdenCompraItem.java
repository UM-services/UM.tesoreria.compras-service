package tesoreria.compras.ordencompra.domain.model;

import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInvalidaException;

import java.math.BigDecimal;

public record OrdenCompraItem(Long id, Integer articuloId, String descripcion, BigDecimal cantidad,
                              BigDecimal precioUnitario, Long imputacionId) {

    public OrdenCompraItem {
        if (articuloId == null || imputacionId == null || cantidad == null || precioUnitario == null
                || cantidad.signum() <= 0 || precioUnitario.signum() < 0) {
            throw new OrdenCompraInvalidaException("El ítem debe tener referencias e importes válidos");
        }
    }

    /**
     * Ítem todavía no persistido: su identidad la asigna la base al guardarlo.
     */
    public static OrdenCompraItem nuevo(Integer articuloId, String descripcion, BigDecimal cantidad,
                                        BigDecimal precioUnitario, Long imputacionId) {
        return new OrdenCompraItem(null, articuloId, descripcion, cantidad, precioUnitario, imputacionId);
    }

    public BigDecimal subtotal() {
        return cantidad.multiply(precioUnitario);
    }
}
