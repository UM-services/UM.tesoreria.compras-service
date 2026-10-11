package tesoreria.compras.slice.articulo.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreArticuloSearchResponse(
        Long articuloId,
        String nombre,
        String descripcion,
        String unidad,
        BigDecimal precio,
        Byte inventariable,
        Long stockMinimo,
        BigDecimal numeroCuenta,
        CoreCuentaArticuloResponse cuenta,
        String tipo,
        Byte directo,
        Byte habilitado,
        String search,
        OffsetDateTime fechaAuditoria,
        String usuarioAuditoria
) {
}
