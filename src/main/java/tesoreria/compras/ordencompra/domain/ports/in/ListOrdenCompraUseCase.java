package tesoreria.compras.ordencompra.domain.ports.in;

import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.PaginaOrdenCompra;

public interface ListOrdenCompraUseCase {

    PaginaOrdenCompra list(OrdenCompraCriteria criteria);
}
