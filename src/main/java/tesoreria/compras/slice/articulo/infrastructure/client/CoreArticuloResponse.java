package tesoreria.compras.slice.articulo.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreArticuloResponse(
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
        CoreCuentaArticuloResponse cuenta
) {
}
