package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import tesoreria.compras.slice.pedidoCompra.domain.model.DependenciaInfo;
import tesoreria.compras.slice.pedidoCompra.domain.model.Solicitante;

public interface ContextoGateway {

    Solicitante getSolicitante(Long userId);

    DependenciaInfo getDependencia(Integer dependenciaId);
}
