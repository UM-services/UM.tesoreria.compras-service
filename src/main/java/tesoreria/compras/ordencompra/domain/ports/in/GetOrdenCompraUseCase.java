package tesoreria.compras.ordencompra.domain.ports.in;

import tesoreria.compras.ordencompra.domain.model.OrdenCompra;

public interface GetOrdenCompraUseCase {

    OrdenCompra get(Long id);
}
