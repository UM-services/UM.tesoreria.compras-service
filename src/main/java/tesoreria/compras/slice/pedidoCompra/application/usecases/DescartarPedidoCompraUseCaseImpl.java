package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.DescartarPedidoCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

@Component
@RequiredArgsConstructor
public class DescartarPedidoCompraUseCaseImpl implements DescartarPedidoCompraUseCase {

    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public PedidoCompra descartar(Integer compraPedidoId, Long usuarioId, String motivo) {
        return compraPedidoGateway.descartar(compraPedidoId, usuarioId, motivo);
    }
}
