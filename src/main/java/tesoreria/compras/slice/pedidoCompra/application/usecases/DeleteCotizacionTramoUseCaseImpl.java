package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.DeleteCotizacionTramoUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CotizacionGateway;

@Component
@RequiredArgsConstructor
public class DeleteCotizacionTramoUseCaseImpl implements DeleteCotizacionTramoUseCase {

    private final CotizacionGateway cotizacionGateway;

    @Override
    public void eliminar(Long cotizacionTramoId) {
        cotizacionGateway.eliminar(cotizacionTramoId);
    }
}
