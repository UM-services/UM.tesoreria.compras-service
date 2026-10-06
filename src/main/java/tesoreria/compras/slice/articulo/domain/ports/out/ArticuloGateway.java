package tesoreria.compras.slice.articulo.domain.ports.out;

import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;

import java.util.List;

public interface ArticuloGateway {

    Articulo getArticuloById(Long articuloId);

    List<ArticuloSearch> searchArticulos(List<String> conditions);
}
