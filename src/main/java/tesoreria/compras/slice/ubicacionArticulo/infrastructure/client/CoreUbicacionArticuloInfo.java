package tesoreria.compras.slice.ubicacionArticulo.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/** Proyección común de los objetos anidados ubicación y cuenta de la respuesta de core. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreUbicacionArticuloInfo(
        Integer ubicacionId,
        BigDecimal numeroCuenta,
        String nombre
) {
}
