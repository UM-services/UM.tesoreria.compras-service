package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.AutorizanteGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoreCompraPedidoAutorizanteFeignClientAdapter implements AutorizanteGateway {

    private final CoreCompraPedidoAutorizanteFeignClient coreCompraPedidoAutorizanteFeignClient;

    @Override
    public List<Integer> getDependenciasAutorizadas(Long userId) {
        try {
            CoreCompraPedidoAutorizanteResponse response =
                    coreCompraPedidoAutorizanteFeignClient.getDependencias(userId);
            return response.dependenciaIds() == null ? List.of() : response.dependenciaIds();
        } catch (FeignException.NotFound exception) {
            // Autorizante sin dependencias configuradas: fail-closed, no un error de infraestructura.
            return List.of();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }
}
