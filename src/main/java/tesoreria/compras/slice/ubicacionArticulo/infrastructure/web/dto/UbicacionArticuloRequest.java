package tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto;

import java.math.BigDecimal;

public record UbicacionArticuloRequest(
        Integer ubicacionId,
        Long articuloId,
        BigDecimal numeroCuenta
) {
}
