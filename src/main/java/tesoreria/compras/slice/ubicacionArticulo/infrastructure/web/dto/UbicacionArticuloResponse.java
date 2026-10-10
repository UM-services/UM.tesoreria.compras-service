package tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto;

import java.math.BigDecimal;

public record UbicacionArticuloResponse(
        Long ubicacionArticuloId,
        Integer ubicacionId,
        Long articuloId,
        BigDecimal numeroCuenta,
        UbicacionRefResponse ubicacion,
        CuentaRefResponse cuenta
) {
}
