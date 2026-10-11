package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.ContextoInicioPedido;
import tesoreria.compras.slice.pedidoCompra.domain.model.DependenciaInfo;
import tesoreria.compras.slice.pedidoCompra.domain.model.Solicitante;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.GetContextoInicioPedidoUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.ContextoGateway;

@Component
@RequiredArgsConstructor
public class GetContextoInicioPedidoUseCaseImpl implements GetContextoInicioPedidoUseCase {

    private final ContextoGateway contextoGateway;

    @Override
    public ContextoInicioPedido getContexto(Long userId) {
        Solicitante solicitante = contextoGateway.getSolicitante(userId);
        DependenciaInfo dependencia = solicitante.dependenciaId() == null
                ? null
                : contextoGateway.getDependencia(solicitante.dependenciaId());
        return new ContextoInicioPedido(solicitante, dependencia);
    }
}
