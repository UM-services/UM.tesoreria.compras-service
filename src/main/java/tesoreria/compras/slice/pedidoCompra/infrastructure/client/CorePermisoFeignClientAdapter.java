package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.PermisoGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CorePermisoFeignClientAdapter implements PermisoGateway {

    private final CorePermisoFeignClient corePermisoFeignClient;

    @Override
    public List<String> getPermisosEfectivos(Long userId) {
        try {
            CorePermisoEfectivoResponse response = corePermisoFeignClient.getPermisosEfectivos(userId);
            return response.permisos() == null ? List.of() : response.permisos();
        } catch (FeignException.NotFound exception) {
            // Usuario inexistente: sin permisos (fail-closed), no un error de infraestructura.
            return List.of();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }
}
