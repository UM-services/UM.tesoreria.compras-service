package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.CreateCotizacionTramoUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CotizacionGateway;

@Component
@RequiredArgsConstructor
public class CreateCotizacionTramoUseCaseImpl implements CreateCotizacionTramoUseCase {

    private final CotizacionGateway cotizacionGateway;

    @Override
    public CotizacionTramo crear(CotizacionTramo tramo) {
        return cotizacionGateway.crear(tramo);
    }
}
