package tesoreria.compras.slice.articulo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;
import tesoreria.compras.slice.articulo.domain.ports.in.GetArticuloByIdUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.SearchArticulosUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticuloService {

    private final GetArticuloByIdUseCase getArticuloByIdUseCase;
    private final SearchArticulosUseCase searchArticulosUseCase;

    public Articulo getArticuloById(Long articuloId) {
        return getArticuloByIdUseCase.getArticuloById(articuloId);
    }

    public List<ArticuloSearch> searchArticulos(List<String> conditions) {
        return searchArticulosUseCase.searchArticulos(conditions);
    }
}
