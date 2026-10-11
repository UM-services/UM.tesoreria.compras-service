package tesoreria.compras.slice.articulo.domain.ports.out;

import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;

import java.util.List;

public interface ArticuloGateway {

    Articulo getArticuloById(Long articuloId);

    List<ArticuloSearch> searchArticulos(List<String> conditions);

    PaginatedResponse<Articulo> getPaginatedByTipo(String tipo, int page, int size);

    Articulo getNewArticulo();

    Articulo createArticulo(Articulo articulo);

    Articulo updateArticulo(Long articuloId, Articulo articulo);

    void deleteArticulo(Long articuloId);
}
