package tesoreria.compras.slice.ubicacionArticulo.domain.model;

import java.math.BigDecimal;

/**
 * Vínculo ubicación↔artículo con la cuenta de imputación. Se guardan solo los
 * nombres de ubicación y cuenta (proyección suficiente para la pantalla Gastos).
 */
public record UbicacionArticulo(
        Long ubicacionArticuloId,
        Integer ubicacionId,
        Long articuloId,
        BigDecimal numeroCuenta,
        String ubicacionNombre,
        String cuentaNombre
) {
}
