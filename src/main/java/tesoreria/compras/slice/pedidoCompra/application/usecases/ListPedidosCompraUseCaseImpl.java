package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.ListPedidosCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListPedidosCompraUseCaseImpl implements ListPedidosCompraUseCase {

    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public List<PedidoCompra> listar(Long solicitanteId) {
        return compraPedidoGateway.listarPorSolicitante(solicitanteId);
    }
}
