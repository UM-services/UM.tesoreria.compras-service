package tesoreria.compras.ordencompra.domain.ports.in;

import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;

public interface TransitionOrdenCompraUseCase {

    OrdenCompra transition(Long id, OrdenCompraEstado estado);
}
