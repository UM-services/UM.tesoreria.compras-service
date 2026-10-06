package tesoreria.compras.slice.articulo.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ArticuloSearch(
        Long articuloId,
        String nombre,
        String descripcion,
        String unidad,
        BigDecimal precio,
        Byte inventariable,
        Long stockMinimo,
        BigDecimal numeroCuenta,
        Cuenta cuenta,
        String tipo,
        Byte directo,
        Byte habilitado,
        String search,
        OffsetDateTime fechaAuditoria,
        String usuarioAuditoria
) {
}
