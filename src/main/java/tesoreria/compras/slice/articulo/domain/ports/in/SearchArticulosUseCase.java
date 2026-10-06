package tesoreria.compras.slice.articulo.domain.ports.in;

import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;

import java.util.List;

public interface SearchArticulosUseCase {

    List<ArticuloSearch> searchArticulos(List<String> conditions);
}
