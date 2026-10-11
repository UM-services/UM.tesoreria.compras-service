package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.ListConsultaPedidosUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListConsultaPedidosUseCaseImpl implements ListConsultaPedidosUseCase {

    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public List<PedidoCompra> listar(PedidoCompraFiltro filtro) {
        return compraPedidoGateway.listar(filtro);
    }
}
