package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.ActualizarPedidoCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

@Component
@RequiredArgsConstructor
public class ActualizarPedidoCompraUseCaseImpl implements ActualizarPedidoCompraUseCase {

    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public PedidoCompra actualizar(Integer compraPedidoId, PedidoCompra pedido) {
        return compraPedidoGateway.actualizar(compraPedidoId, pedido);
    }
}
