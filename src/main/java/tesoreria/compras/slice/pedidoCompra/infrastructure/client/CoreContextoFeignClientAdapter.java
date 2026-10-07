package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.DependenciaInfo;
import tesoreria.compras.slice.pedidoCompra.domain.model.Solicitante;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.ContextoGateway;

@Component
@RequiredArgsConstructor
public class CoreContextoFeignClientAdapter implements ContextoGateway {

    private final CoreUsuarioFeignClient coreUsuarioFeignClient;
    private final CoreDependenciaFeignClient coreDependenciaFeignClient;

    @Override
    public Solicitante getSolicitante(Long userId) {
        try {
            CoreUsuarioResponse response = coreUsuarioFeignClient.getMe(userId);
            return new Solicitante(response.userId(), response.nombre(), response.login(),
                    response.dependenciaId());
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public DependenciaInfo getDependencia(Integer dependenciaId) {
        try {
            CoreDependenciaResponse response = coreDependenciaFeignClient.getById(dependenciaId);
            return new DependenciaInfo(
                    response.dependenciaId(),
                    response.nombre(),
                    response.facultadId(),
                    response.facultad() == null ? null : response.facultad().nombre(),
                    response.geograficaId(),
                    response.geografica() == null ? null : response.geografica().nombre());
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }
}
