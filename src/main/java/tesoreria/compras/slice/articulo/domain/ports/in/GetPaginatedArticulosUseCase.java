package tesoreria.compras.slice.articulo.domain.ports.in;

import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.domain.model.Articulo;

public interface GetPaginatedArticulosUseCase {

    PaginatedResponse<Articulo> getPaginated(String tipo, int page, int size);
}
