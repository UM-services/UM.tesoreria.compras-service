package tesoreria.compras.slice.articulo.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreCuentaArticuloResponse(
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
