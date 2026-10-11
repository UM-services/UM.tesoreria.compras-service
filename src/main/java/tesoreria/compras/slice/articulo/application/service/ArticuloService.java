package tesoreria.compras.slice.articulo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;
import tesoreria.compras.slice.articulo.domain.ports.in.CreateArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.DeleteArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.GetArticuloByIdUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.GetNewArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.GetPaginatedArticulosUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.SearchArticulosUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.UpdateArticuloUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticuloService {

    private final GetArticuloByIdUseCase getArticuloByIdUseCase;
    private final SearchArticulosUseCase searchArticulosUseCase;
    private final GetPaginatedArticulosUseCase getPaginatedArticulosUseCase;
    private final GetNewArticuloUseCase getNewArticuloUseCase;
    private final CreateArticuloUseCase createArticuloUseCase;
    private final UpdateArticuloUseCase updateArticuloUseCase;
    private final DeleteArticuloUseCase deleteArticuloUseCase;

    public Articulo getArticuloById(Long articuloId) {
        return getArticuloByIdUseCase.getArticuloById(articuloId);
    }

    public List<ArticuloSearch> searchArticulos(List<String> conditions) {
        return searchArticulosUseCase.searchArticulos(conditions);
    }

    public PaginatedResponse<Articulo> getPaginated(String tipo, int page, int size) {
        return getPaginatedArticulosUseCase.getPaginated(tipo, page, size);
    }

    public Articulo getNewArticulo() {
        return getNewArticuloUseCase.getNewArticulo();
    }

    public Articulo createArticulo(Articulo articulo) {
        return createArticuloUseCase.createArticulo(articulo);
    }

    public Articulo updateArticulo(Long articuloId, Articulo articulo) {
        return updateArticuloUseCase.updateArticulo(articuloId, articulo);
    }

    public void deleteArticulo(Long articuloId) {
        deleteArticuloUseCase.deleteArticulo(articuloId);
    }
}
