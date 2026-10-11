package tesoreria.compras.slice.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.ports.in.GetPaginatedArticulosUseCase;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

@Component
@RequiredArgsConstructor
public class GetPaginatedArticulosUseCaseImpl implements GetPaginatedArticulosUseCase {

    private final ArticuloGateway articuloGateway;

    @Override
    public PaginatedResponse<Articulo> getPaginated(String tipo, int page, int size) {
        return articuloGateway.getPaginatedByTipo(tipo, page, size);
    }
}
