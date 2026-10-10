package tesoreria.compras.slice.ubicacionArticulo.domain.ports.in;

import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;

import java.util.List;

public interface GetUbicacionArticulosByArticuloUseCase {

    List<UbicacionArticulo> getByArticulo(Long articuloId);
}
