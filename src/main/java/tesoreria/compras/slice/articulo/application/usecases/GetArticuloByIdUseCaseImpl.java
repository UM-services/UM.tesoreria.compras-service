package tesoreria.compras.slice.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.ports.in.GetArticuloByIdUseCase;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

@Component
@RequiredArgsConstructor
public class GetArticuloByIdUseCaseImpl implements GetArticuloByIdUseCase {

    private final ArticuloGateway articuloGateway;

    @Override
    public Articulo getArticuloById(Long articuloId) {
        return articuloGateway.getArticuloById(articuloId);
    }
}
