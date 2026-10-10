package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.EstimarPedidoCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class EstimarPedidoCompraUseCaseImpl implements EstimarPedidoCompraUseCase {

    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public PedidoCompra estimar(Integer compraPedidoId, Long usuarioId, BigDecimal monto, String fuente) {
        return compraPedidoGateway.estimar(compraPedidoId, usuarioId, monto, fuente);
    }
}
