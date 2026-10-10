package tesoreria.compras.slice.ubicacionArticulo.domain.ports.out;

import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;

import java.util.List;

public interface UbicacionArticuloGateway {

    List<UbicacionArticulo> getByArticulo(Long articuloId);

    UbicacionArticulo save(UbicacionArticulo ubicacionArticulo);
}
