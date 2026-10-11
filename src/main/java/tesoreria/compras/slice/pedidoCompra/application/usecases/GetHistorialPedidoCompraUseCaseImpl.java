package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraHistorial;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.GetHistorialPedidoCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.HistorialGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetHistorialPedidoCompraUseCaseImpl implements GetHistorialPedidoCompraUseCase {

    private final HistorialGateway historialGateway;

    @Override
    public List<PedidoCompraHistorial> listar(Integer compraPedidoId) {
        return historialGateway.listar(compraPedidoId);
    }
}
