package tesoreria.compras.slice.proveedor.domain.ports.in;

import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;

public interface GetPaginatedProveedoresUseCase {

    PaginatedResponse<Proveedor> getPaginated(int page, int size);
}
