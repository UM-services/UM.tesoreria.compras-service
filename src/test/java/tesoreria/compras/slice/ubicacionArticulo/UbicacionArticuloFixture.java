package tesoreria.compras.slice.ubicacionArticulo;

import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.client.CoreUbicacionArticuloInfo;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.client.CoreUbicacionArticuloResponse;

import java.math.BigDecimal;

public final class UbicacionArticuloFixture {

    private UbicacionArticuloFixture() {
    }

    public static UbicacionArticulo ubicacionArticulo() {
        return new UbicacionArticulo(
                50L, 1, 101L, new BigDecimal("20101090099"), "Rectorado", "Obligaciones a Pagar");
    }

    public static CoreUbicacionArticuloResponse coreResponse() {
        return new CoreUbicacionArticuloResponse(
                50L, 1, 101L, new BigDecimal("20101090099"),
                new CoreUbicacionArticuloInfo(1, null, "Rectorado"),
                new CoreUbicacionArticuloInfo(null, new BigDecimal("20101090099"), "Obligaciones a Pagar"));
    }
}
