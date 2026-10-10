package tesoreria.compras.slice.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.articulo.domain.ports.in.DeleteArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

@Component
@RequiredArgsConstructor
public class DeleteArticuloUseCaseImpl implements DeleteArticuloUseCase {

    private final ArticuloGateway articuloGateway;

    @Override
    public void deleteArticulo(Long articuloId) {
        articuloGateway.deleteArticulo(articuloId);
    }
}
