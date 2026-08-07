package tesoreria.compras.ordencompra.domain.ports.in;

import tesoreria.compras.ordencompra.domain.model.OrdenCompra;

public interface GetOrdenCompraByNumeroUseCase {

    OrdenCompra getByNumero(String numero);
}
