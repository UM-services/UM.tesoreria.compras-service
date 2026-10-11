package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.GetPedidoCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

@Component
@RequiredArgsConstructor
public class GetPedidoCompraUseCaseImpl implements GetPedidoCompraUseCase {

    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public PedidoCompra getById(Integer compraPedidoId) {
        return compraPedidoGateway.getById(compraPedidoId);
    }
}
