package tesoreria.compras.slice.ubicacion.domain.ports.out;

import tesoreria.compras.slice.ubicacion.domain.model.Ubicacion;

import java.util.List;

public interface UbicacionGateway {

    List<Ubicacion> getUbicaciones();
}
