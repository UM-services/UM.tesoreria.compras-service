package tesoreria.compras.slice.articulo.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ArticuloSearchResponse(
        Long articuloId,
        String nombre,
        String descripcion,
        String unidad,
        BigDecimal precio,
        Byte inventariable,
        Long stockMinimo,
        BigDecimal numeroCuenta,
        CuentaResponse cuenta,
        String tipo,
        Byte directo,
        Byte habilitado,
        String search,
        OffsetDateTime fechaAuditoria,
        String usuarioAuditoria
) {
}
