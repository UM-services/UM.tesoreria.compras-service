package tesoreria.compras.slice.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.ports.in.GetNewArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

@Component
@RequiredArgsConstructor
public class GetNewArticuloUseCaseImpl implements GetNewArticuloUseCase {

    private final ArticuloGateway articuloGateway;

    @Override
    public Articulo getNewArticulo() {
        return articuloGateway.getNewArticulo();
    }
}
