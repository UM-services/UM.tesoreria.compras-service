package tesoreria.compras.slice.ubicacion.domain.ports.in;

import tesoreria.compras.slice.ubicacion.domain.model.Ubicacion;

import java.util.List;

public interface GetUbicacionesUseCase {

    List<Ubicacion> getUbicaciones();
}
