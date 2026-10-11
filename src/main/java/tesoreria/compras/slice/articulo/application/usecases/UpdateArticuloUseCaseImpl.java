package tesoreria.compras.slice.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.ports.in.UpdateArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

@Component
@RequiredArgsConstructor
public class UpdateArticuloUseCaseImpl implements UpdateArticuloUseCase {

    private final ArticuloGateway articuloGateway;

    @Override
    public Articulo updateArticulo(Long articuloId, Articulo articulo) {
        return articuloGateway.updateArticulo(articuloId, articulo);
    }
}
