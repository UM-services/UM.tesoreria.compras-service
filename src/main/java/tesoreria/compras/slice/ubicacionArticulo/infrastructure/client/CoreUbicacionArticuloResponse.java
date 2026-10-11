package tesoreria.compras.slice.ubicacionArticulo.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreUbicacionArticuloResponse(
        Long ubicacionArticuloId,
        Integer ubicacionId,
        Long articuloId,
        BigDecimal numeroCuenta,
        CoreUbicacionArticuloInfo ubicacion,
        CoreUbicacionArticuloInfo cuenta
) {
}
