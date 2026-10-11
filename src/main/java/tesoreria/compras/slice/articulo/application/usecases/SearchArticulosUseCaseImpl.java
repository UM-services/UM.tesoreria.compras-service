package tesoreria.compras.slice.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;
import tesoreria.compras.slice.articulo.domain.ports.in.SearchArticulosUseCase;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchArticulosUseCaseImpl implements SearchArticulosUseCase {

    private final ArticuloGateway articuloGateway;

    @Override
    public List<ArticuloSearch> searchArticulos(List<String> conditions) {
        return articuloGateway.searchArticulos(conditions);
    }
}
