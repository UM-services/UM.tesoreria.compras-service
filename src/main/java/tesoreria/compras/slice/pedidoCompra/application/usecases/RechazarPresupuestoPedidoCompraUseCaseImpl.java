package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.RechazarPresupuestoPedidoCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

@Component
@RequiredArgsConstructor
public class RechazarPresupuestoPedidoCompraUseCaseImpl implements RechazarPresupuestoPedidoCompraUseCase {

    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public PedidoCompra rechazar(Integer compraPedidoId, Long usuarioId, String motivo) {
        return compraPedidoGateway.rechazarPresupuesto(compraPedidoId, usuarioId, motivo);
    }
}
