package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.ListCotizacionTramosUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CotizacionGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCotizacionTramosUseCaseImpl implements ListCotizacionTramosUseCase {

    private final CotizacionGateway cotizacionGateway;

    @Override
    public List<CotizacionTramo> listar() {
        return cotizacionGateway.listar();
    }
}
