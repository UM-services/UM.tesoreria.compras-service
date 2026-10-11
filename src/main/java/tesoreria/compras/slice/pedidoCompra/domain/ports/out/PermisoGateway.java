package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import java.util.List;

public interface PermisoGateway {

    List<String> getPermisosEfectivos(Long userId);
}
