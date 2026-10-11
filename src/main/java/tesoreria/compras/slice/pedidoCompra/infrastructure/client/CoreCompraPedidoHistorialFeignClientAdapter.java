package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraHistorial;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.HistorialGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoreCompraPedidoHistorialFeignClientAdapter implements HistorialGateway {

    private final CoreCompraPedidoHistorialFeignClient coreCompraPedidoHistorialFeignClient;

    @Override
    public List<PedidoCompraHistorial> listar(Integer compraPedidoId) {
        try {
            return coreCompraPedidoHistorialFeignClient.listar(compraPedidoId).stream()
                    .map(this::toDomain)
                    .toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    private PedidoCompraHistorial toDomain(CoreCompraPedidoHistorialResponse response) {
        return new PedidoCompraHistorial(
                response.compraPedidoHistorialId(),
                response.compraPedidoId(),
                response.estado(),
                response.usuarioId(),
                response.observacion(),
                response.fecha());
    }
}
