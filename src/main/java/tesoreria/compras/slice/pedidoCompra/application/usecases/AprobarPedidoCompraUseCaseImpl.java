package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.AprobarPedidoCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

@Component
@RequiredArgsConstructor
public class AprobarPedidoCompraUseCaseImpl implements AprobarPedidoCompraUseCase {

    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public PedidoCompra aprobar(Integer compraPedidoId, Long autorizanteId) {
        return compraPedidoGateway.aprobar(compraPedidoId, autorizanteId);
    }
}
