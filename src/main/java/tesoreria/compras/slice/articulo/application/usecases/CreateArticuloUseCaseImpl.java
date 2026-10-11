package tesoreria.compras.slice.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.ports.in.CreateArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

@Component
@RequiredArgsConstructor
public class CreateArticuloUseCaseImpl implements CreateArticuloUseCase {

    private final ArticuloGateway articuloGateway;

    @Override
    public Articulo createArticulo(Articulo articulo) {
        return articuloGateway.createArticulo(articulo);
    }
}
