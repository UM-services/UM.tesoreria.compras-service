package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.UpdateCotizacionTramoUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CotizacionGateway;

@Component
@RequiredArgsConstructor
public class UpdateCotizacionTramoUseCaseImpl implements UpdateCotizacionTramoUseCase {

    private final CotizacionGateway cotizacionGateway;

    @Override
    public CotizacionTramo actualizar(Long cotizacionTramoId, CotizacionTramo tramo) {
        return cotizacionGateway.actualizar(cotizacionTramoId, tramo);
    }
}
