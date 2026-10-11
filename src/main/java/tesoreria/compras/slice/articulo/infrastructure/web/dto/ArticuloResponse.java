package tesoreria.compras.slice.articulo.infrastructure.web.dto;

import java.math.BigDecimal;

public record ArticuloResponse(
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
        CuentaResponse cuenta
) {
}
