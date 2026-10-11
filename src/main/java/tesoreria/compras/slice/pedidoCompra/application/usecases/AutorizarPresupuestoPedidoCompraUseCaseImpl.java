package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.AutorizarPresupuestoPedidoCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

@Component
@RequiredArgsConstructor
public class AutorizarPresupuestoPedidoCompraUseCaseImpl implements AutorizarPresupuestoPedidoCompraUseCase {

    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public PedidoCompra autorizar(Integer compraPedidoId, Long usuarioId) {
        return compraPedidoGateway.autorizarPresupuesto(compraPedidoId, usuarioId);
    }
}
