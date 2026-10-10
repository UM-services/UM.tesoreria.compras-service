package tesoreria.compras.slice.ubicacion;

import tesoreria.compras.slice.ubicacion.domain.model.Ubicacion;
import tesoreria.compras.slice.ubicacion.infrastructure.client.CoreUbicacionResponse;

public final class UbicacionFixture {

    private UbicacionFixture() {
    }

    public static Ubicacion ubicacion() {
        return new Ubicacion(1, "Rectorado", 10, 20);
    }

    public static CoreUbicacionResponse coreUbicacionResponse() {
        return new CoreUbicacionResponse(1, "Rectorado", 10, 20);
    }
}
