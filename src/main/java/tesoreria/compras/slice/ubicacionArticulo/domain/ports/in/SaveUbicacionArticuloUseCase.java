package tesoreria.compras.slice.ubicacionArticulo.domain.ports.in;

import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;

public interface SaveUbicacionArticuloUseCase {

    UbicacionArticulo save(UbicacionArticulo ubicacionArticulo);
}
