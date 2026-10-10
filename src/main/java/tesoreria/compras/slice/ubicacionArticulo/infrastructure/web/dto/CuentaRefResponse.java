package tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto;

import java.math.BigDecimal;

public record CuentaRefResponse(
        BigDecimal numeroCuenta,
        String nombre
) {
}
