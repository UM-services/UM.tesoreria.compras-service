package tesoreria.compras.slice.articulo.domain.model;

import java.math.BigDecimal;

public record Articulo(
        Long articuloId,
        String nombre,
        String descripcion,
        String unidad,
        BigDecimal precio,
        Byte inventariable,
        Long stockMinimo,
        BigDecimal numeroCuenta,
        String tipo,
        Byte directo,
        Byte habilitado,
        Cuenta cuenta
) {
}
