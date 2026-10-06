package tesoreria.compras.slice.articulo.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CuentaResponse(
        BigDecimal numeroCuenta,
        String nombre,
        Byte integradora,
        Integer grado,
        BigDecimal grado1,
        BigDecimal grado2,
        BigDecimal grado3,
        BigDecimal grado4,
        Integer geograficaId,
        OffsetDateTime fechaBloqueo,
        Byte visible,
        Long cuentaContableId
) {
}
