package tesoreria.compras.slice.articulo.domain.ports.in;

import tesoreria.compras.slice.articulo.domain.model.Articulo;

public interface GetArticuloByIdUseCase {

    Articulo getArticuloById(Long articuloId);
}
