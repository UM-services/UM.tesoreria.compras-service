package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.ListBandejaEnvioUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.AutorizanteGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListBandejaEnvioUseCaseImpl implements ListBandejaEnvioUseCase {

    private final AutorizanteGateway autorizanteGateway;
    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public List<PedidoCompra> listar(Long userId, String estado) {
        List<Integer> dependencias = autorizanteGateway.getDependenciasAutorizadas(userId);
        if (dependencias == null || dependencias.isEmpty()) {
            return List.of();
        }
        return compraPedidoGateway.listar(
                new PedidoCompraFiltro(estado, null, null, dependencias, null, null));
    }
}
